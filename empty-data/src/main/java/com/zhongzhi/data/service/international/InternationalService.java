package com.zhongzhi.data.service.international;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.InternationalRedisKeyConstant;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.entity.customer.CustomerConsume;
import com.zhongzhi.data.entity.international.InternationalCheck;
import com.zhongzhi.data.entity.international.InternationalCheckStatistics;
import com.zhongzhi.data.entity.sys.FileUpload;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.enums.UserCheckTypeEnum;
import com.zhongzhi.data.param.InternationalCheckQueryParam;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.customer.CustomerConsumeService;
import com.zhongzhi.data.service.direct.DirectInternationalService;
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
 * 国际号码检测实现类
 * @author liuh
 * @date 2022年6月8日
 */
@Slf4j
@Service
public class InternationalService {

	private static final Logger logger = LoggerFactory.getLogger(InternationalService.class);

	@Value("${online.time}")
	private String onlineTime;

	@Autowired
	private FileUploadService fileUploadService;
	
	@Value("${http.international.file.url}")
	private String internationalFileUrl;
	
	@Value("${http.international.process.url}")
	private String processUrl;
	
	@Value("${http.international.temp.file.delete.url}")
	private String deleteTempFileUrl;
	
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
	private InternationalCvsFilePathService internationalCvsFilePathService;
	
	@Autowired
	private InternationalCheckService internationalCheckService;
	
	@Autowired
	private DirectInternationalService directInternationalService;

	public ApiResult internationalCheckByFile(Long fileId,String countryCode) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		try {
			// 查询文件上传记录
			FileUpload fileUpload = fileUploadService.findOne(fileId);
			if(fileUpload == null) {
				return ApiResult.fail("待检测文件不存在");
			}
			
			InternationalCheck international = getInternationalCheckData(customer, fileUpload,countryCode);
			int counts = internationalCheckService.saveOne(international);
			if(counts != 1) {
				log.error("{}, 开始国际检测失败，检测记录入库异常，international:{}",customer.getCustomerId(),JSON.toJSONString(international));
				return ApiResult.fail("检测记录创建失败");
			}
			
			// 消费记录入库
	        CustomerConsume consume = getCustomerConsumeData(customer, fileId, 0L,Long.valueOf(fileUpload.getFileRows()),
	        		CustomerConsume.ConsumeType.DEDUCTION_SUCCESS.getValue());
	        counts = customerConsumeService.saveOne(consume);
	        if(counts != 1) {
				log.error("{}, 开始国际检测失败，消耗记录入库异常，consume:{}",customer.getCustomerId(),JSON.toJSONString(consume));
				return ApiResult.fail("消耗记录创建失败");
			}
			
			ApiResult<InternationalRunTestDomian> result = invoke(fileUpload,countryCode);
			if(!result.isSuccess()) {
				return ApiResult.fail(result.getMsg());
			}
			
			return result;
		} catch (Exception e) {
			log.error("{}， 开始国际检测异常，fileId:{},info:",customer.getCustomerId(),fileId,e);
			return ApiResult.fail("系统异常");
		}
	}
	
	public ApiResult getTestProcessMobile(Long fileId,String sendID) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		//文件检测是否出现异常
        String exceptions = redisClient.get(String.format(InternationalRedisKeyConstant.EXCEPTION_KEY, customer.getCustomerId(),fileId));
        // 出现异常终止检测
        if (StringUtils.isNotBlank(exceptions)  && exceptions.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//清除redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
        	//删除临时文件
        	deleteTempFile(customer.getCustomerId(),fileId);
			return ApiResult.fail("获取进度失败，检测异常，请联系客服处理");
        }
        //文件是否检测完成
        String runStatus = redisClient.get(String.format(InternationalRedisKeyConstant.THE_RUN_KEY, customer.getCustomerId(),fileId));
        //检测完成
        if (StringUtils.isNotBlank(runStatus)  && runStatus.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//清除redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
			return ApiResult.fail(ApiCode.FILE_CHECK_SUCCESS);
        }
        //获取文件已经检测的条数
        String testCounts = redisClient.get(String.format(InternationalRedisKeyConstant.SUCCEED_TEST_COUNT_KEY, customer.getCustomerId(),fileId));        
    	//获取检测完成该的号码
        JSONArray resultList = new JSONArray();
        try {
        	//获取已经检测成功的号码用于前端显示
        	String tempStr = redisClient.get(String.format(InternationalRedisKeyConstant.MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
        	if(StringUtils.isBlank(tempStr)){
        		testCounts = "36";
        		//获取用户文件里默认的号码用户前端显示
        		String defaultStr = redisClient.get(String.format(InternationalRedisKeyConstant.DEFAULT_MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
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
                		tempJson.put("mobile", firstThreeList[j] + "1234" + lastFourList[j]);
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
        String fileCounts = redisClient.get(String.format(InternationalRedisKeyConstant.SUCCEED_CLEARING_COUNT_KEY, customer.getCustomerId(),fileId));
        JSONObject json = new JSONObject();
        json.put("testCounts", testCounts);
        json.put("fileCounts", StringUtils.isBlank(fileCounts)?"0":fileCounts);
        json.put("mobileList", resultList);
        json.put("fileCode", fileId);
        
		return ApiResult.ok(json);
	}
	
	private ApiResult<InternationalRunTestDomian> invoke(FileUpload fileUpload,String countryCode) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerId", fileUpload.getCustomerId());
		paramMap.put("internationalId", fileUpload.getId());
		paramMap.put("totalNumber", fileUpload.getFileRows());
		paramMap.put("sourceFileName", fileUpload.getFileName());
		paramMap.put("uploadPath", fileUpload.getFileUploadUrl());
		paramMap.put("countryCode", countryCode);
		paramMap.put("productType", "");
		String response = okhttpService.post(internationalFileUrl, paramMap);
		if(StringUtils.isBlank(response)) {
			log.error("{}, 调用open国际在线检测接口失败，返回结果为空，param:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap));
			return ApiResult.fail("系统异常");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, 调用open国际检测接口失败，param:{},response:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap),response);
			return ApiResult.fail(jsonObject.getString("msg"));
		}
		
		return ApiResult.ok(JSONObject.parseObject(jsonObject.getString("data"), InternationalRunTestDomian.class));
	}
	
	/**
     * 清空条数注销锁
     *
     * @param userId
     * @param mobile
     */
    private void clearLockAndCountForRun(Long customerId, Long internationalId) {
        String identifier = redisClient.get(String.format(InternationalRedisKeyConstant.REDIS_LOCK_IDENTIFIER_KEY, customerId,internationalId));
        // 清空redis
        redisClient.remove(String.format(InternationalRedisKeyConstant.TEST_COUNT_KEY, customerId,internationalId));
        redisClient.remove(String.format(InternationalRedisKeyConstant.GENERATE_RESULTS_KEY, customerId,internationalId));
        this.releaseLock(String.format(InternationalRedisKeyConstant.THE_TEST_FUN_KEY, customerId,internationalId), identifier); // 注销锁
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
    
    private CustomerConsume getCustomerConsumeData(CustomerInfoVo customerInfoVo,Long internationalId,Long balance,Long rows,Integer consumeType) {
		CustomerConsume customerConsume = new CustomerConsume();
		customerConsume.setAgentId(customerInfoVo.getAgentId())
		.setId(snowflake.nextId())
        .setConsumeNumber(rows)
        .setCustomerId(customerInfoVo.getCustomerId())
        .setName(customerInfoVo.getCustomerName())
        .setPhone(customerInfoVo.getPhone())
        .setVersion(0)
        .setCategory(ProductTypeEnum.INTERNATIONAL.getCode())
        .setConsumeType(consumeType)
        .setEmptyId(internationalId)
        .setOpeningBalance(balance + customerConsume.getConsumeNumber())
        .setClosingBalance(balance);
		return customerConsume;
	}

	public ApiResult<Boolean> delete(Long id) {
        int i = internationalCvsFilePathService.delete(id);
		if (i<=0) {
			logger.error("客户id：{}，国际检测记录-删除失败。id:{}", ThreadLocalContainer.getCustomerId(), id);
			return ApiResult.fail(ApiCode.DAO_EXCEPTION);
		} else {
			logger.info("客户id：{}，国际检测记录-删除成功。id:{}", ThreadLocalContainer.getCustomerId(), id);
			return ApiResult.ok();
		}
	}

	public PageInfo<InternationalCheckQueryVo> getInternationalPageList(InternationalCheckQueryParam param) {
		// 设置查询参数
		Long customerId = ThreadLocalContainer.getCustomerId();
		param.setCustomerId(customerId);
		
		PageHelper.startPage(param.getPage(), param.getSize());
		List<InternationalCheckQueryVo> list = internationalCvsFilePathService.pageList(param);
		
		list.forEach(i -> {
			if(i.getCheckStatus() == 0) {
				int sec = (int) ((new Date().getTime() - i.getCreateTime().getTime()) / 1000);
				int process = (sec * 6) >= i.getTotalNumber().intValue() ? 99 : (int)((sec * 6 * 100) / i.getTotalNumber());
				i.setCheckProcess(String.valueOf(process));
			}
		});

		return new PageInfo<InternationalCheckQueryVo>(list);
	}

	public ApiResult<InternationalCheckQueryVo> getLatestInternational() {
		Customer customer = ThreadLocalContainer.getCustomer();
		return ApiResult.ok(internationalCvsFilePathService.findLastOne(customer.getId()));
	}

	public List<InternationalCheckStatistics> statistics(int year, int month) {
		String from = year + "-" + month + "-01 00:00:00";
		String end;
		if (month == 12) {
			end = (year + 1) + "-" + "01" + "-01 00:00:00";
		} else {
			end = year + "-" + (month + 1) + "-01 00:00:00";
		}
		Long customerId = ThreadLocalContainer.getCustomerId();
		return internationalCvsFilePathService.statisticList(customerId, from, end);
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
			log.error("{}, 调用open删除国际检测临时文件接口失败，返回结果为空，param:{}",customerId,JSON.toJSONString(paramMap));
			return ;
		}
		
		log.info("{}, 调用open删除国际检测临时文件接口成功，fileUrl:{}",customerId,fileUpload.getFileUploadUrl());
	}
	
	private InternationalCheck getInternationalCheckData(CustomerInfoVo customer,FileUpload fileUpload,String countryCode) {
		InternationalCheck international = new InternationalCheck();
		international.setStatus(InternationalCheck.InternationalCheckStatus.INIT.getStatus())
        .setIllegalNumber(null)
        .setTotalNumber(Long.valueOf(fileUpload.getFileRows()))
        .setAgentId(customer.getAgentId())
        .setAgentName(customer.getCompanyName())
        .setCustomerId(customer.getCustomerId())
        .setFileUrl(fileUpload.getFileUploadUrl())
        .setFileName(fileUpload.getFileName())
        .setFileSize(fileUpload.getFileSize())
        .setId(fileUpload.getId())
        .setDeleted(0)
        .setVersion(0)
        .setMd5(fileUpload.getFileMd5())
        .setCountryCode(countryCode)
        .setRemark(UserCheckTypeEnum.UPLOAD.getName());
		return international;
	}
}
