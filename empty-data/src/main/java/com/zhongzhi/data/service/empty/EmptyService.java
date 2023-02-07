package com.zhongzhi.data.service.empty;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.CommonConstant;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.EmptyRedisKeyConstant;
import com.zhongzhi.data.entity.ApiSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.entity.customer.CustomerConsume;
import com.zhongzhi.data.entity.empty.EmptyCheck;
import com.zhongzhi.data.entity.empty.EmptyCheckStatistics;
import com.zhongzhi.data.entity.sys.FileUpload;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.enums.UserCheckTypeEnum;
import com.zhongzhi.data.mapper.CvsFilePathMapper;
import com.zhongzhi.data.mapper.empty.EmptyCheckMapper;
import com.zhongzhi.data.param.EmptyCheckQueryParam;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.ApiSettingsService;
import com.zhongzhi.data.service.customer.CustomerConsumeService;
import com.zhongzhi.data.service.http.OkhttpService;
import com.zhongzhi.data.service.sys.FileUploadService;
import com.zhongzhi.data.util.*;
import com.zhongzhi.data.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

/**
 * 空号检测实现类
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@Service
public class EmptyService {

	private static final Logger logger = LoggerFactory.getLogger(EmptyService.class);

	@Autowired
	private FileUploadService fileUploadService;
	
	@Autowired
	private EmptyCheckService emptyCheckService;
	
	@Value("${http.empty.file.url}")
	private String emptyFileUrl;
	
	@Value("${http.empty.api.url}")
	private String emptyApiUrl;

	@Value("${http.empty.temp.file.delete.url}")
	private String deleteTempFileUrl;

	@Value("${online.time}")
	private String onlineTime;
	
	@Autowired
	private OkhttpService okhttpService;
	
	@Autowired
	private RedisClient redisClient;
	
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private EmptyCheckMapper emptyCheckMapper;

	@Autowired
	private Snowflake snowflake;

	@Autowired
	private CustomerConsumeService customerConsumeService;

	@Autowired
	private ApiSettingsService apiSettingsService;

	@Autowired
	private CvsFilePathMapper cvsFilePathMapper;

	public ApiResult emptyCheckByFile(Long fileId) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		try {
			// 查询文件上传记录
			FileUpload fileUpload = fileUploadService.findOne(fileId);
			if(fileUpload == null) {
				return ApiResult.fail("待检测文件不存在");
			}
			
			EmptyCheck empty = getEmptyCheckData(customer, fileUpload);
			int counts = emptyCheckService.saveOne(empty);
			if(counts != 1) {
				log.error("{}, 开始空号检测失败，检测记录入库异常，empty:{}",customer.getCustomerId(),JSON.toJSONString(empty));
				return ApiResult.fail("检测记录创建失败");
			}
			
			// 消费记录入库
	        CustomerConsume consume = getCustomerConsumeData(customer, fileId, 0L,Long.valueOf(fileUpload.getFileRows()),
	        		CustomerConsume.ConsumeType.DEDUCTION_SUCCESS.getValue());
	        counts = customerConsumeService.saveOne(consume);
	        if(counts != 1) {
				log.error("{}, 开始空号检测失败，消耗记录入库异常，consume:{}",customer.getCustomerId(),JSON.toJSONString(consume));
				return ApiResult.fail("消耗记录创建失败");
			}

			ApiResult<RunTestDomian> result = invoke(fileUpload);
			if(!result.isSuccess()) {
				return ApiResult.fail(result.getMsg());
			}
			
			return result;
		} catch (Exception e) {
			log.error("{}， 开始空号检测异常，fileId:{},info:",customer.getCustomerId(),fileId,e);
			return ApiResult.fail("系统异常");
		}
	}
	
	public ApiResult getTestProcessMobile(Long fileId) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		//文件检测是否出现异常
        String exceptions = redisClient.get(String.format(EmptyRedisKeyConstant.EXCEPTION_KEY, customer.getCustomerId(),fileId));
        // 出现异常终止检测
        if (StringUtils.isNotBlank(exceptions)  && exceptions.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//清除redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
        	//删除临时文件
        	deleteTempFile(customer.getCustomerId(),fileId);
			return ApiResult.fail("获取进度失败，检测异常，请联系客服处理");
        }
        //文件是否检测完成
        String runStatus = redisClient.get(String.format(EmptyRedisKeyConstant.THE_RUN_KEY, customer.getCustomerId(),fileId));
        //检测完成
        if (StringUtils.isNotBlank(runStatus)  && runStatus.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//清除redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
			return ApiResult.fail(ApiCode.FILE_CHECK_SUCCESS);
        }
        //获取文件已经检测的条数
        String testCounts = redisClient.get(String.format(EmptyRedisKeyConstant.SUCCEED_TEST_COUNT_KEY, customer.getCustomerId(),fileId));        
    	//获取检测完成该的号码
        JSONArray resultList = new JSONArray();
        try {
        	//获取已经检测成功的号码用于前端显示
        	String tempStr = redisClient.get(String.format(EmptyRedisKeyConstant.MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
        	if(StringUtils.isBlank(tempStr)){
        		testCounts = "36";
        		//获取用户文件里默认的号码用户前端显示
        		String defaultStr = redisClient.get(String.format(EmptyRedisKeyConstant.DEFAULT_MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
        		if(StringUtils.isNotBlank(defaultStr)){
        			JSONArray list = JSONArray.parseArray(defaultStr);
        			//随机获取36个数
                	int[] intList = CommonUtils.randomCommon(0, list.size()-1, list.size()<36?list.size():36);   
                	for(int i: intList){
                		resultList.add(list.get(i));
                	}
        		}else{
        			//随机获取36个数
                	int[] firstThreeList = CommonUtils.randomCommon(130, 189, 36);  
                	//随机获取36个数
                	int[] lastFourList = CommonUtils.randomCommon(1000, 9999, 36); 
                	for(int j=0;j<36;j++){
                		JSONObject tempJson = new JSONObject();
                		tempJson.put("mobile", firstThreeList[j] + "****" + lastFourList[j]);
                		tempJson.put("color", j%6==0?"yellow":"blue");
                		resultList.add(tempJson);
                	}
        		}
        	}else{        		
        		JSONArray list = JSONArray.parseArray(tempStr);
            	//随机获取36个数
            	int[] intList = CommonUtils.randomCommon(0, list.size()-1, list.size()<36?list.size():36);   
            	for(int i: intList){
            		resultList.add(list.get(i));
            	}
        	}        	
        	
		} catch (Exception e) {
			log.error("{},获取文件：{} 检测进度异常,异常信息为：",customer.getCustomerId(),fileId,e);
			return ApiResult.fail("获取进度失败，检测异常，请联系客服处理");
		}
        
        if(StringUtils.isBlank(testCounts) || "0".equals(testCounts)){
        	testCounts = "36";
        }
        //需要检测的号码总条数
        String fileCounts = redisClient.get(String.format(EmptyRedisKeyConstant.SUCCEED_CLEARING_COUNT_KEY, customer.getCustomerId(),fileId));
        JSONObject json = new JSONObject();
        json.put("testCounts", testCounts);
        json.put("fileCounts", StringUtils.isBlank(fileCounts)?"0":fileCounts);
        json.put("mobileList", resultList);
        json.put("fileCode", fileId);
        
		return ApiResult.ok(json);
	}
	
	public ApiResult<List<UnnMobileNewStatus>> batchCheckNew(String mobiles){
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		// 获取用户api账号信息
		ApiSettings apiSettings = apiSettingsService.findOne(customer.getCustomerId());
		if(apiSettings == null) {
			return ApiResult.fail(ApiCode.BUSINESS_EXCEPTION,"api账号信息不存在");
		}

		return invokeApi(apiSettings, customer.getCustomerId(), mobiles);
	}

	private ApiResult<List<UnnMobileNewStatus>> invokeApi(ApiSettings apiSettings,Long customerId,String mobiles) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", apiSettings.getAppId());
		paramMap.put("appKey", apiSettings.getAppKey());
		paramMap.put("mobiles", mobiles);
		String response = okhttpService.post(emptyApiUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open空号检测api接口失败，返回结果为空，param:{}",customerId,JSON.toJSONString(paramMap));
			return ApiResult.fail("系统异常");
		}

		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, 调用open空号检测api接口失败，param:{},response:{}",customerId,JSON.toJSONString(paramMap),response);
			return ApiResult.fail(jsonObject.getString("msg"));
		}

		return ApiResult.ok(JSONArray.parseArray(jsonObject.getString("data"), UnnMobileNewStatus.class));
	}

	private ApiResult<RunTestDomian> invoke(FileUpload fileUpload) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerId", fileUpload.getCustomerId());
		paramMap.put("emptyId", fileUpload.getId());
		paramMap.put("totalNumber", fileUpload.getFileRows());
		paramMap.put("sourceFileName", fileUpload.getFileName());
		paramMap.put("uploadPath", fileUpload.getFileUploadUrl());
		String response = okhttpService.post(emptyFileUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open空号检测接口失败，返回结果为空，param:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap));
			return ApiResult.fail("系统异常");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, 调用open空号检测接口失败，param:{},response:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap),response);
			return ApiResult.fail(jsonObject.getString("msg"));
		}
		
		return ApiResult.ok(JSONObject.parseObject(jsonObject.getString("data"), RunTestDomian.class));
	}
	
	private void deleteTempFile(Long customerId,Long fileId) {
		FileUpload fileUpload = fileUploadService.findOne(fileId);
		if(fileUpload == null) {
			return ;
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fileUrl", fileUpload.getFileUploadUrl());
		String response = okhttpService.post(deleteTempFileUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open删除空号检测临时文件接口失败，返回结果为空，param:{}",customerId,JSON.toJSONString(paramMap));
			return ;
		}
		
		log.info("{}, 调用open删除空号检测临时文件接口成功，fileUrl:{}",customerId,fileUpload.getFileUploadUrl());
	}
	
	private EmptyCheck getEmptyCheckData(CustomerInfoVo customer,FileUpload fileUpload) {
		EmptyCheck empty = new EmptyCheck();
		empty.setStatus(EmptyCheck.EmptyCheckStatus.TO_DEDUCT.getStatus())
        .setIllegalNumber(null)
        .setTotalNumber(Long.valueOf(fileUpload.getFileRows()))
        .setAgentId(customer.getAgentId())
        .setAgentName(customer.getCompanyName())
        .setCustomerId(customer.getCustomerId())
        .setEmptyNumber(null)
        .setFileUrl(fileUpload.getFileUploadUrl())
        .setName(fileUpload.getFileName())
        .setRealNumber(null)
        .setRiskNumber(null)
        .setSilentNumber(null)
        .setSize(fileUpload.getFileSize())
        .setSendId(null)
        .setId(fileUpload.getId())
        .setCacheFinish(1)
        .setRetryCount(0)
        .setDeleted(0)
        .setVersion(0)
        .setMd5(fileUpload.getFileMd5())
        .setCategory(ProductTypeEnum.EMPTY.getCode())
        .setRemark(UserCheckTypeEnum.UPLOAD.getName());
		return empty;
	}
	
	/**
     * 清空条数注销锁
     *
     * @param userId
     * @param mobile
     */
    private void clearLockAndCountForRun(Long customerId, Long emptyId) {
        String identifier = redisClient.get(String.format(EmptyRedisKeyConstant.REDIS_LOCK_IDENTIFIER_KEY, customerId,emptyId));
        // 清空redis
        redisClient.remove(String.format(EmptyRedisKeyConstant.TEST_COUNT_KEY, customerId,emptyId));
        redisClient.remove(String.format(EmptyRedisKeyConstant.GENERATE_RESULTS_KEY, customerId,emptyId));
        this.releaseLock(String.format(EmptyRedisKeyConstant.THE_TEST_FUN_KEY, customerId,emptyId), identifier); // 注销锁
        redisClient.remove(identifier);
    }
    
    /**
     * 释放锁
     *
     * @param lockName   锁的key
     * @param identifier 释放锁的标识
     * @return
     */
    private boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = jedisPool.getResource();
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }

    /**
     * 空号检测记录分页列表
     * @date 2021/11/5
     * @param emptyQueryParam
     * @return com.zhongzhi.data.vo.Paging<com.zhongzhi.data.vo.EmptyCheckQueryVo>
     */
	public PageInfo<EmptyCheckQueryVo> getEmptyPageList(EmptyCheckQueryParam emptyQueryParam) {
		// 根据上线时间判断是老数据还是新数据
		Long onlineLongTime = DateUtils.getLongTime(DateUtils.parseDate(onlineTime));
		Date createTime = emptyQueryParam.getCreateTimeFrom();
		Long createLongTime = DateUtils.getLongTime(createTime);
		Date endTime = emptyQueryParam.getCreateTimeEnd();
		Long endLongTime = DateUtils.getLongTime(endTime);

		// 设置查询参数
		Long customerId = ThreadLocalContainer.getCustomerId();
		emptyQueryParam.setCustomerId(customerId);
		if (emptyQueryParam.getCreateTimeEnd() != null) {
			emptyQueryParam.setCreateTimeEnd(DateUtil.endOfDay(emptyQueryParam.getCreateTimeEnd()));
		}

		return this.queryNewData(emptyQueryParam);
	}

	/**
	 * 查询新老数据混合的情况
	 * @date 2021/11/15
	 * @param emptyQueryParam
	 * @return PageInfo<EmptyCheckQueryVo>
	 */
	private PageInfo<EmptyCheckQueryVo> queryOldMixNewData(EmptyCheckQueryParam emptyQueryParam) {
		Date onlineTimeDate = DateUtils.parseDate(onlineTime);
		// 处理特殊情况。某一天00:00:00的数据，因为数据库是<=
		Date middleLine = DateUtils.getYesterdayEnd(onlineTimeDate);
		Date originEndTime = emptyQueryParam.getCreateTimeEnd();

		// 老数据
		emptyQueryParam.setCreateTimeEnd(DateUtil.endOfDay(middleLine));
		List<EmptyCheckQueryVo> list = emptyCheckMapper.getEmptyCheckPageList(emptyQueryParam);
		list.forEach((emptyCheck) -> {
			emptyCheck.setIsOldData(1);
		});

		// 新数据
		emptyQueryParam.setCreateTimeFrom(DateUtil.beginOfDay(onlineTimeDate));
		emptyQueryParam.setCreateTimeEnd(DateUtil.endOfDay(originEndTime));
		List<EmptyCheckQueryVo> list2 = cvsFilePathMapper.findList(emptyQueryParam);
		list2.forEach((emptyCheck)->{
			emptyCheck.setIsOldData(0);
		});
		list2.addAll(list);

		// 手动进行分页
		PageInfo<EmptyCheckQueryVo> info = PageInfoUtils.list2PageInfo(list2, emptyQueryParam.getPage(), emptyQueryParam.getSize());
		return info;
	}

	/**
	 * 查询的历史检测数据是新数据
	 * @date 2021/11/15
	 * @param emptyQueryParam
	 * @return PageInfo<EmptyCheckQueryVo>
	 */
	private PageInfo<EmptyCheckQueryVo> queryNewData(EmptyCheckQueryParam emptyQueryParam) {
		PageInfo<EmptyCheckQueryVo> info;
		PageHelper.startPage(emptyQueryParam.getPage(), emptyQueryParam.getSize());
		List<EmptyCheckQueryVo> list = cvsFilePathMapper.findList(emptyQueryParam);
		list.forEach((emptyCheck)->{
			emptyCheck.setIsOldData(0);
		});
		info = new PageInfo<>(list);
		return info;
	}

	/**
	 * 查询的历史检测数据是老数据
	 * @date 2021/11/15
	 * @param emptyQueryParam
	 * @return PageInfo<EmptyCheckQueryVo>
	 */
	private PageInfo<EmptyCheckQueryVo> queryOldData(EmptyCheckQueryParam emptyQueryParam) {
		PageHelper.startPage(emptyQueryParam.getPage(), emptyQueryParam.getSize());
		List<EmptyCheckQueryVo> list = emptyCheckMapper.getEmptyCheckPageList(emptyQueryParam);
		list.forEach((emptyCheck) -> {
			emptyCheck.setIsOldData(1);
		});
		PageInfo<EmptyCheckQueryVo> info = new PageInfo<>(list);
		return info;
	}

	/**
	 * 获取最近正在运行的记录
	 * @date 2021/11/9
	 * @param
	 * @return PageInfo<EmptyCheckQueryVo>
	 */
	public List<EmptyCheckQueryVo> getRunningList() {
		Date endTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		Date fromTime = calendar.getTime();

		Long customerId = ThreadLocalContainer.getCustomerId();

		List<EmptyCheckQueryVo> list = emptyCheckMapper.getRunningList(customerId, fromTime, endTime);
		return list;
	}

    private CustomerConsume getCustomerConsumeData(CustomerInfoVo customerInfoVo,Long emptyId,Long balance,Long rows,Integer consumeType) {
		CustomerConsume customerConsume = new CustomerConsume();
		customerConsume.setAgentId(customerInfoVo.getAgentId())
		.setId(snowflake.nextId())
        .setConsumeNumber(rows)
        .setCustomerId(customerInfoVo.getCustomerId())
        .setName(customerInfoVo.getCustomerName())
        .setPhone(customerInfoVo.getPhone())
        .setVersion(0)
        .setCategory(ProductTypeEnum.EMPTY.getCode())
        .setConsumeType(consumeType)
        .setEmptyId(emptyId)
        .setOpeningBalance(balance + customerConsume.getConsumeNumber())
        .setClosingBalance(balance);
		return customerConsume;
	}

	/**
	 * 获取最新的一条空号检测记录
	 * @date 2021/11/9
	 * @param
	 * @return ApiResult<EmptyCheck>
	 */
	public ApiResult<EmptyCheckQueryVo> getLatestEmpty() {
		Customer customer = ThreadLocalContainer.getCustomer();

		EmptyCheckQueryVo vo = cvsFilePathMapper.findLast(customer.getId());
		if (vo != null) {
			vo.setIsOldData(0);
		}
		return ApiResult.ok(vo);
	}

	public List<EmptyCheckStatistics> statistics(int year, int month) {
		String from = year + "-" + month + "-01 00:00:00";
		String end;
		if (month == 12) {
			end = (year + 1) + "-" + "01" + "-01 00:00:00";
		} else {
			end = year + "-" + (month + 1) + "-01 00:00:00";
		}
		Long customerId = ThreadLocalContainer.getCustomerId();
		return emptyCheckMapper.statistics(customerId, from, end);
	}

	/**
	 * 空号检测记录（在线测试）-列表
	 * @date 2021/11/11
	 * @param
	 * @return PageInfo<EmptyCheck>
	 */
	public PageInfo<EmptyCheck> getTestRecord(int page, int size) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(page, size);
		List<EmptyCheck> list = emptyCheckMapper.getTestRecord(page, size, customerId);
		PageInfo<EmptyCheck> info = new PageInfo<>(list);
		return info;
	}

	/**
	 * 删除空号检测记录
	 * @date 2021/11/12
	 * @param id
	 * @return ApiResult<Boolean>
	 */
	public ApiResult<Boolean> delete(Long id, Integer isOldData) {
		if (isOldData.equals(0)) {
		    // 新数据
            int i = cvsFilePathMapper.delete(id);
			if (i<=0) {
				logger.error("客户id：{}，空号检测记录cvs-删除失败。id:{}", ThreadLocalContainer.getCustomerId(), id);
				return ApiResult.fail(ApiCode.DAO_EXCEPTION);
			} else {
				logger.info("客户id：{}，空号检测记录cvs-删除成功。id:{}", ThreadLocalContainer.getCustomerId(), id);
				return ApiResult.ok();
			}
		} else {
		    // 老数据
			int i = emptyCheckMapper.delete(id);
			if (i<=0) {
				logger.error("客户id：{}，空号检测记录-删除失败。id:{}", ThreadLocalContainer.getCustomerId(), id);
				return ApiResult.fail(ApiCode.DAO_EXCEPTION);
			} else {
				logger.info("客户id：{}，空号检测记录-删除成功。id:{}", ThreadLocalContainer.getCustomerId(), id);
				return ApiResult.ok();
			}
		}

	}
}
