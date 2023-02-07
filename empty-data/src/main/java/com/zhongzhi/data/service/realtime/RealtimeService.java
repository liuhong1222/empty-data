package com.zhongzhi.data.service.realtime;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.RealtimeRedisKeyConstant;
import com.zhongzhi.data.entity.ApiSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.entity.customer.CustomerConsume;
import com.zhongzhi.data.entity.realtime.RealtimeCheck;
import com.zhongzhi.data.entity.realtime.RealtimeCheckStatistics;
import com.zhongzhi.data.entity.sys.FileUpload;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.MobileStatusQueryStateEnum;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.enums.UserCheckTypeEnum;
import com.zhongzhi.data.mapper.realtime.RealtimeCheckMapper;
import com.zhongzhi.data.mapper.realtime.RealtimeCvsFilePathMapper;
import com.zhongzhi.data.param.RealtimeCheckQueryParam;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实时检测实现类
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@Service
public class RealtimeService {

	private static final Logger logger = LoggerFactory.getLogger(RealtimeService.class);

	@Value("${online.time}")
	private String onlineTime;

	@Autowired
	private FileUploadService fileUploadService;
	
	@Autowired
	private RealtimeCheckService realtimeCheckService;
	
	@Value("${http.realtime.file.url}")
	private String realtimeFileUrl;
	
	@Value("${http.realtime.temp.file.delete.url}")
	private String deleteTempFileUrl;
	
	@Value("${http.realtime.api.url}")
	private String realtimeApiUrl;
	
	@Value("${http.realtime.stardard.api.url}")
	private String realtimeStardardApiUrl;
	
	@Value("${http.realtime.single.api.url}")
	private String realtimeSingleApiUrl;
	
	@Autowired
	private OkhttpService okhttpService;
	
	@Autowired
	private RedisClient redisClient;
	
	@Autowired
	private JedisPool jedisPool;
	
	@Autowired
	private Snowflake snowflake;
	
	@Autowired
	private CustomerConsumeService customerConsumeService;
	
	@Autowired
	private ApiSettingsService apiSettingsService;

	@Autowired
	private RealtimeCheckMapper realtimeCheckMapper;

	@Autowired
	private RealtimeCvsFilePathMapper realtimeCvsFilePathMapper;

	public ApiResult realtimeCheckByFile(Long fileId) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		try {
			// 查询文件上传记录
			FileUpload fileUpload = fileUploadService.findOne(fileId);
			if(fileUpload == null) {
				return ApiResult.fail("待检测文件不存在");
			}
			
			RealtimeCheck realtimeCheck = getRealtimeCheckData(customer, fileUpload);
			int counts = realtimeCheckService.saveOne(realtimeCheck);
			if(counts != 1) {
				log.error("{}, 开始实时检测失败，检测记录入库异常，realtimeCheck:{}",customer.getCustomerId(),JSON.toJSONString(realtimeCheck));
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
			log.error("{}， 开始实时检测异常，fileId:{},info:",customer.getCustomerId(),fileId,e);
			return ApiResult.fail("系统异常");
		}
	}
	
	public ApiResult getTestProcessMobile(Long fileId) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		//文件检测是否出现异常
        String exceptions = redisClient.get(String.format(RealtimeRedisKeyConstant.EXCEPTION_KEY, customer.getCustomerId(),fileId));
        // 出现异常终止检测
        if (StringUtils.isNotBlank(exceptions)  && exceptions.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//清除redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
        	//删除临时文件
        	deleteTempFile(customer.getCustomerId(),fileId);
			return ApiResult.fail("获取进度失败，检测异常，请联系客服处理");
        }
        //文件是否检测完成
        String runStatus = redisClient.get(String.format(RealtimeRedisKeyConstant.THE_RUN_KEY, customer.getCustomerId(),fileId));
        //检测完成
        if (StringUtils.isNotBlank(runStatus)  && runStatus.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//清除redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
			return ApiResult.fail(ApiCode.FILE_CHECK_SUCCESS);
        }
        //获取文件已经检测的条数
        String testCounts = redisClient.get(String.format(RealtimeRedisKeyConstant.SUCCEED_TEST_COUNT_KEY, customer.getCustomerId(),fileId));        
    	//获取检测完成该的号码
        JSONArray resultList = new JSONArray();
        try {
        	//获取已经检测成功的号码用于前端显示
        	String tempStr = redisClient.get(String.format(RealtimeRedisKeyConstant.MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
        	if(StringUtils.isBlank(tempStr)){
        		testCounts = "36";
        		//获取用户文件里默认的号码用户前端显示
        		String defaultStr = redisClient.get(String.format(RealtimeRedisKeyConstant.DEFAULT_MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
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
        String fileCounts = redisClient.get(String.format(RealtimeRedisKeyConstant.SUCCEED_CLEARING_COUNT_KEY, customer.getCustomerId(),fileId));
        JSONObject json = new JSONObject();
        json.put("testCounts", testCounts);
        json.put("fileCounts", StringUtils.isBlank(fileCounts)?"0":fileCounts);
        json.put("mobileList", resultList);
        json.put("fileCode", fileId);
        
		return ApiResult.ok(json);
	}
	
	public ApiResult<RealtimeResult> mobileStatusStatic(String mobile,boolean isStardard){
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		// 获取用户api账号信息
		ApiSettings apiSettings = apiSettingsService.findOne(customer.getCustomerId());
		if(apiSettings == null) {
			return ApiResult.fail(ApiCode.BUSINESS_EXCEPTION,"api账号信息不存在");
		}
		
		return invokeApi(customer.getCustomerId(), mobile, apiSettings, isStardard);
	}
	
	public ApiResult<String> realtimeCheckBySingle(String mobile) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		return invokeBySingle(customer.getCustomerId(), mobile);
	}
	
	private ApiResult<RealtimeResult> invokeApi(Long customerId,String mobile,ApiSettings apiSettings,boolean isStardard) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", apiSettings.getAppId());
		paramMap.put("appKey", apiSettings.getAppKey());
		paramMap.put("mobiles", mobile);
		String response = okhttpService.post(isStardard?realtimeStardardApiUrl:realtimeApiUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open实时检测api接口失败，返回结果为空，param:{}",customerId,JSON.toJSONString(paramMap));
			return ApiResult.fail("系统异常");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, 调用open实时检测api接口失败，param:{},response:{}",customerId,JSON.toJSONString(paramMap),response);
			return ApiResult.fail(jsonObject.getString("msg"));
		}
		
		return ApiResult.ok(JSONObject.parseObject(jsonObject.getString("data"), RealtimeResult.class));
	}
	
	private ApiResult<RunTestDomian> invoke(FileUpload fileUpload) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerId", fileUpload.getCustomerId());
		paramMap.put("realtimeId", fileUpload.getId());
		paramMap.put("totalNumber", fileUpload.getFileRows());
		paramMap.put("sourceFileName", fileUpload.getFileName());
		paramMap.put("uploadPath", fileUpload.getFileUploadUrl());
		String response = okhttpService.post(realtimeFileUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open实时在线检测接口失败，返回结果为空，param:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap));
			return ApiResult.fail("系统异常");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, 调用open实时检测接口失败，param:{},response:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap),response);
			return ApiResult.fail(jsonObject.getString("msg"));
		}
		
		return ApiResult.ok(JSONObject.parseObject(jsonObject.getString("data"), RunTestDomian.class));
	}
	
	private ApiResult<String> invokeBySingle(Long customerId,String mobile) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerId", customerId);
		paramMap.put("mobile", mobile);
		String response = okhttpService.post(realtimeSingleApiUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open实时检测单个号码接口失败，返回结果为空，param:{}",customerId,JSON.toJSONString(paramMap));
			return ApiResult.fail("系统异常");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, 调用open实时检测单个号码接口失败，param:{},response:{}",customerId,JSON.toJSONString(paramMap),response);
			return ApiResult.fail(jsonObject.getString("msg"));
		}
		
		return ApiResult.ok(jsonObject.getString("data"));
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
			log.error("{}, 调用open删除实时检测临时文件接口失败，返回结果为空，param:{}",customerId,JSON.toJSONString(paramMap));
			return ;
		}
		
		log.info("{}, 调用open删除实时检测临时文件接口成功，fileUrl:{}",customerId,fileUpload.getFileUploadUrl());
	}
	
	private RealtimeCheck getRealtimeCheckData(CustomerInfoVo customer,FileUpload fileUpload) {
		RealtimeCheck realtimeCheck = new RealtimeCheck();
		realtimeCheck.setStatus(RealtimeCheck.RealtimeCheckStatus.INIT.getStatus())
        .setIllegalNumber(null)
        .setTotalNumber(Long.valueOf(fileUpload.getFileRows()))
        .setAgentId(customer.getAgentId())
        .setAgentName(customer.getCompanyName())
        .setCustomerId(customer.getCustomerId())
        .setFileUrl(fileUpload.getFileUploadUrl())
        .setName(fileUpload.getFileName())
        .setSize(fileUpload.getFileSize())
        .setId(fileUpload.getId())
        .setRetryCount(0)
        .setDeleted(0)
        .setVersion(0)
        .setMd5(fileUpload.getFileMd5())
        .setCheckType(0)
        .setRemark(UserCheckTypeEnum.UPLOAD.getName());
		return realtimeCheck;
	}
	
	/**
     * 清空条数注销锁
     *
     * @param userId
     * @param mobile
     */
    private void clearLockAndCountForRun(Long customerId, Long emptyId) {
        String identifier = redisClient.get(String.format(RealtimeRedisKeyConstant.REDIS_LOCK_IDENTIFIER_KEY, customerId,emptyId));
        // 清空redis
        redisClient.remove(String.format(RealtimeRedisKeyConstant.TEST_COUNT_KEY, customerId,emptyId));
        redisClient.remove(String.format(RealtimeRedisKeyConstant.GENERATE_RESULTS_KEY, customerId,emptyId));
        this.releaseLock(String.format(RealtimeRedisKeyConstant.THE_TEST_FUN_KEY, customerId,emptyId), identifier); // 注销锁
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
    
    private CustomerConsume getCustomerConsumeData(CustomerInfoVo customerInfoVo,Long emptyId,Long balance,Long rows,Integer consumeType) {
		CustomerConsume customerConsume = new CustomerConsume();
		customerConsume.setAgentId(customerInfoVo.getAgentId())
		.setId(snowflake.nextId())
        .setConsumeNumber(rows)
        .setCustomerId(customerInfoVo.getCustomerId())
        .setName(customerInfoVo.getCustomerName())
        .setPhone(customerInfoVo.getPhone())
        .setVersion(0)
        .setCategory(ProductTypeEnum.REALTIME.getCode())
        .setConsumeType(consumeType)
        .setEmptyId(emptyId)
        .setOpeningBalance(balance + customerConsume.getConsumeNumber())
        .setClosingBalance(balance);
		return customerConsume;
	}

	/**
	 * 实时检测-删除
	 * @date 2021/11/15
	 * @param id
	 * @return ApiResult<Boolean>
	 */
	public ApiResult<Boolean> delete(Long id) {
		// 新数据
        int i = realtimeCvsFilePathMapper.delete(id);
		if (i<=0) {
			logger.error("客户id：{}，实时检测记录-删除失败。id:{}", ThreadLocalContainer.getCustomerId(), id);
			return ApiResult.fail(ApiCode.DAO_EXCEPTION);
		} else {
			logger.info("客户id：{}，实时检测记录-删除成功。id:{}", ThreadLocalContainer.getCustomerId(), id);
			return ApiResult.ok();
		}
	}

	/**
	 * 实时检测记录分页列表
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return PageInfo<RealtimeCheckQueryVo>
	 */
	public PageInfo<RealtimeCheckQueryVo> getRealtimePageList(RealtimeCheckQueryParam realtimeCheckQueryParam) {
		// 根据上线时间判断是老数据还是新数据
		Long onlineLongTime = DateUtils.getLongTime(DateUtils.parseDate(onlineTime));
		Date createTime = realtimeCheckQueryParam.getCreateTimeFrom();
		Long createLongTime = DateUtils.getLongTime(createTime);
		Date endTime = realtimeCheckQueryParam.getCreateTimeEnd();
		Long endLongTime = DateUtils.getLongTime(endTime);

		// 设置查询参数
		Long customerId = ThreadLocalContainer.getCustomerId();
		realtimeCheckQueryParam.setCustomerId(customerId);
		if (realtimeCheckQueryParam.getCreateTimeEnd() != null) {
			realtimeCheckQueryParam.setCreateTimeEnd(DateUtil.endOfDay(realtimeCheckQueryParam.getCreateTimeEnd()));
		}

		return this.queryNewData(realtimeCheckQueryParam);
	}

	/**
	 *  查询的历史实时检测数据是老数据
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return PageInfo<RealtimeCheckQueryVo>
	 */
	private PageInfo<RealtimeCheckQueryVo> queryOldData(RealtimeCheckQueryParam realtimeCheckQueryParam) {
		PageHelper.startPage(realtimeCheckQueryParam.getPage(), realtimeCheckQueryParam.getSize());
		List<RealtimeCheckQueryVo> list = realtimeCheckMapper.getRealtimePageList(realtimeCheckQueryParam);
		list.forEach((emptyCheck) -> {
			emptyCheck.setIsOldData(1);
		});
		PageInfo<RealtimeCheckQueryVo> info = new PageInfo<>(list);
		return info;
	}

	/**
	 * 查询的历史实时检测数据是新数据
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return PageInfo<RealtimeCheckQueryVo>
	 */
	private PageInfo<RealtimeCheckQueryVo> queryNewData(RealtimeCheckQueryParam realtimeCheckQueryParam) {
		PageInfo<RealtimeCheckQueryVo> info;
		PageHelper.startPage(realtimeCheckQueryParam.getPage(), realtimeCheckQueryParam.getSize());
		List<RealtimeCheckQueryVo> list = realtimeCvsFilePathMapper.findList(realtimeCheckQueryParam);
		list.forEach((emptyCheck)->{
			emptyCheck.setIsOldData(0);
		});
		info = new PageInfo<>(list);
		return info;
	}

	/**
	 * 查询实时检测新老数据混合的情况
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return PageInfo<RealtimeCheckQueryVo>
	 */
	private PageInfo<RealtimeCheckQueryVo> queryOldMixNewData(RealtimeCheckQueryParam realtimeCheckQueryParam) {
		Date onlineTimeDate = DateUtils.parseDate(onlineTime);
		// 处理特殊情况。某一天00:00:00的数据，因为数据库是<=
		Date middleLine = DateUtils.getYesterdayEnd(onlineTimeDate);
		Date originEndTime = realtimeCheckQueryParam.getCreateTimeEnd();

		// 老数据
		realtimeCheckQueryParam.setCreateTimeEnd(DateUtil.endOfDay(middleLine));
		List<RealtimeCheckQueryVo> list = realtimeCheckMapper.getRealtimePageList(realtimeCheckQueryParam);
		list.forEach((emptyCheck) -> {
			emptyCheck.setIsOldData(1);
		});

		// 新数据
		realtimeCheckQueryParam.setCreateTimeFrom(DateUtil.beginOfDay(onlineTimeDate));
		realtimeCheckQueryParam.setCreateTimeEnd(DateUtil.endOfDay(originEndTime));
		List<RealtimeCheckQueryVo> list2 = realtimeCvsFilePathMapper.findList(realtimeCheckQueryParam);
		list2.forEach((emptyCheck)->{
			emptyCheck.setIsOldData(0);
		});
		list2.addAll(list);

		// 手动进行分页
		PageInfo<RealtimeCheckQueryVo> info = PageInfoUtils.list2PageInfo(list2, realtimeCheckQueryParam.getPage(), realtimeCheckQueryParam.getSize());
		return info;
	}

	/**
	 * 获取最新的一条实时检测记录
	 * @date 2021/11/15
	 * @param
	 * @return ApiResult<RealtimeCheck>
	 */
	public ApiResult<RealtimeCheckQueryVo> getLatestRealtime() {
		// 判断是否是新客户，新客户直接查新表，老客户再判断
		Long onlineLongTime = DateUtils.getLongTime(DateUtils.parseDate(onlineTime));
		Customer customer = ThreadLocalContainer.getCustomer();
		Long createTime = DateUtils.getLongTime(customer.getCreateTime());

		// 新客户，数据是新的
		RealtimeCheckQueryVo vo = realtimeCvsFilePathMapper.findLast(customer.getId());
		if (vo != null) {
			vo.setIsOldData(0);
		}
		return ApiResult.ok(vo);
	}

	public List<RealtimeCheckStatistics> statistics(int year, int month) {
		String from = year + "-" + month + "-01 00:00:00";
		String end;
		if (month == 12) {
			end = (year + 1) + "-" + "01" + "-01 00:00:00";
		} else {
			end = year + "-" + (month + 1) + "-01 00:00:00";
		}
		Long customerId = ThreadLocalContainer.getCustomerId();
		return realtimeCheckMapper.statistics(customerId, from, end);
	}
}
