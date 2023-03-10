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
 * ?????????????????????
 * @author liuh
 * @date 2021???11???4???
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
		// ??????????????????
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		try {
			// ????????????????????????
			FileUpload fileUpload = fileUploadService.findOne(fileId);
			if(fileUpload == null) {
				return ApiResult.fail("????????????????????????");
			}
			
			RealtimeCheck realtimeCheck = getRealtimeCheckData(customer, fileUpload);
			int counts = realtimeCheckService.saveOne(realtimeCheck);
			if(counts != 1) {
				log.error("{}, ??????????????????????????????????????????????????????realtimeCheck:{}",customer.getCustomerId(),JSON.toJSONString(realtimeCheck));
				return ApiResult.fail("????????????????????????");
			}
			
			// ??????????????????
	        CustomerConsume consume = getCustomerConsumeData(customer, fileId, 0L,Long.valueOf(fileUpload.getFileRows()),
	        		CustomerConsume.ConsumeType.DEDUCTION_SUCCESS.getValue());
	        counts = customerConsumeService.saveOne(consume);
	        if(counts != 1) {
				log.error("{}, ??????????????????????????????????????????????????????consume:{}",customer.getCustomerId(),JSON.toJSONString(consume));
				return ApiResult.fail("????????????????????????");
			}
			
			ApiResult<RunTestDomian> result = invoke(fileUpload);
			if(!result.isSuccess()) {
				return ApiResult.fail(result.getMsg());
			}
			
			return result;
		} catch (Exception e) {
			log.error("{}??? ???????????????????????????fileId:{},info:",customer.getCustomerId(),fileId,e);
			return ApiResult.fail("????????????");
		}
	}
	
	public ApiResult getTestProcessMobile(Long fileId) {
		// ??????????????????
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		//??????????????????????????????
        String exceptions = redisClient.get(String.format(RealtimeRedisKeyConstant.EXCEPTION_KEY, customer.getCustomerId(),fileId));
        // ????????????????????????
        if (StringUtils.isNotBlank(exceptions)  && exceptions.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//??????redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
        	//??????????????????
        	deleteTempFile(customer.getCustomerId(),fileId);
			return ApiResult.fail("?????????????????????????????????????????????????????????");
        }
        //????????????????????????
        String runStatus = redisClient.get(String.format(RealtimeRedisKeyConstant.THE_RUN_KEY, customer.getCustomerId(),fileId));
        //????????????
        if (StringUtils.isNotBlank(runStatus)  && runStatus.equals(Constant.FILE_TEST_FAILED_CODE)) {
        	//??????redis
        	this.clearLockAndCountForRun(customer.getCustomerId(), fileId);
			return ApiResult.fail(ApiCode.FILE_CHECK_SUCCESS);
        }
        //?????????????????????????????????
        String testCounts = redisClient.get(String.format(RealtimeRedisKeyConstant.SUCCEED_TEST_COUNT_KEY, customer.getCustomerId(),fileId));        
    	//??????????????????????????????
        JSONArray resultList = new JSONArray();
        try {
        	//???????????????????????????????????????????????????
        	String tempStr = redisClient.get(String.format(RealtimeRedisKeyConstant.MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
        	if(StringUtils.isBlank(tempStr)){
        		testCounts = "36";
        		//??????????????????????????????????????????????????????
        		String defaultStr = redisClient.get(String.format(RealtimeRedisKeyConstant.DEFAULT_MOBILE_DISPLAY_KEY, customer.getCustomerId(),fileId));
        		if(StringUtils.isNotBlank(defaultStr)){
        			JSONArray list = JSONArray.parseArray(defaultStr);
        			//????????????36??????
                	int[] intList = CommonUtils.randomCommon(0, list.size()-1, list.size()<36?list.size():36);   
                	for(int i: intList){
                		resultList.add(list.get(i));
                	}
        		}else{
        			//????????????36??????
                	int[] firstThreeList = CommonUtils.randomCommon(130, 189, 36);  
                	//????????????36??????
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
            	//????????????36??????
            	int[] intList = CommonUtils.randomCommon(0, list.size()-1, list.size()<36?list.size():36);   
            	for(int i: intList){
            		resultList.add(list.get(i));
            	}
        	}        	
        	
		} catch (Exception e) {
			log.error("{},???????????????{} ??????????????????,??????????????????",customer.getCustomerId(),fileId,e);
			return ApiResult.fail("?????????????????????????????????????????????????????????");
		}
        
        if(StringUtils.isBlank(testCounts) || "0".equals(testCounts)){
        	testCounts = "36";
        }
        //??????????????????????????????
        String fileCounts = redisClient.get(String.format(RealtimeRedisKeyConstant.SUCCEED_CLEARING_COUNT_KEY, customer.getCustomerId(),fileId));
        JSONObject json = new JSONObject();
        json.put("testCounts", testCounts);
        json.put("fileCounts", StringUtils.isBlank(fileCounts)?"0":fileCounts);
        json.put("mobileList", resultList);
        json.put("fileCode", fileId);
        
		return ApiResult.ok(json);
	}
	
	public ApiResult<RealtimeResult> mobileStatusStatic(String mobile,boolean isStardard){
		// ??????????????????
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		// ????????????api????????????
		ApiSettings apiSettings = apiSettingsService.findOne(customer.getCustomerId());
		if(apiSettings == null) {
			return ApiResult.fail(ApiCode.BUSINESS_EXCEPTION,"api?????????????????????");
		}
		
		return invokeApi(customer.getCustomerId(), mobile, apiSettings, isStardard);
	}
	
	public ApiResult<String> realtimeCheckBySingle(String mobile) {
		// ??????????????????
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
			log.error("{}, ??????open????????????api????????????????????????????????????param:{}",customerId,JSON.toJSONString(paramMap));
			return ApiResult.fail("????????????");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, ??????open????????????api???????????????param:{},response:{}",customerId,JSON.toJSONString(paramMap),response);
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
			log.error("{}, ??????open??????????????????????????????????????????????????????param:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap));
			return ApiResult.fail("????????????");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, ??????open???????????????????????????param:{},response:{}",fileUpload.getCustomerId(),JSON.toJSONString(paramMap),response);
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
			log.error("{}, ??????open????????????????????????????????????????????????????????????param:{}",customerId,JSON.toJSONString(paramMap));
			return ApiResult.fail("????????????");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(200 != jsonObject.getIntValue("code")) {
			log.error("{}, ??????open???????????????????????????????????????param:{},response:{}",customerId,JSON.toJSONString(paramMap),response);
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
			log.error("{}, ??????open??????????????????????????????????????????????????????????????????param:{}",customerId,JSON.toJSONString(paramMap));
			return ;
		}
		
		log.info("{}, ??????open?????????????????????????????????????????????fileUrl:{}",customerId,fileUpload.getFileUploadUrl());
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
     * ?????????????????????
     *
     * @param userId
     * @param mobile
     */
    private void clearLockAndCountForRun(Long customerId, Long emptyId) {
        String identifier = redisClient.get(String.format(RealtimeRedisKeyConstant.REDIS_LOCK_IDENTIFIER_KEY, customerId,emptyId));
        // ??????redis
        redisClient.remove(String.format(RealtimeRedisKeyConstant.TEST_COUNT_KEY, customerId,emptyId));
        redisClient.remove(String.format(RealtimeRedisKeyConstant.GENERATE_RESULTS_KEY, customerId,emptyId));
        this.releaseLock(String.format(RealtimeRedisKeyConstant.THE_TEST_FUN_KEY, customerId,emptyId), identifier); // ?????????
        redisClient.remove(identifier);
    }
    
    /**
     * ?????????
     *
     * @param lockName   ??????key
     * @param identifier ??????????????????
     * @return
     */
    private boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = jedisPool.getResource();
            while (true) {
                // ??????lock?????????????????????
                conn.watch(lockKey);
                // ?????????????????????value???????????????????????????????????????????????????????????????
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
	 * ????????????-??????
	 * @date 2021/11/15
	 * @param id
	 * @return ApiResult<Boolean>
	 */
	public ApiResult<Boolean> delete(Long id) {
		// ?????????
        int i = realtimeCvsFilePathMapper.delete(id);
		if (i<=0) {
			logger.error("??????id???{}?????????????????????-???????????????id:{}", ThreadLocalContainer.getCustomerId(), id);
			return ApiResult.fail(ApiCode.DAO_EXCEPTION);
		} else {
			logger.info("??????id???{}?????????????????????-???????????????id:{}", ThreadLocalContainer.getCustomerId(), id);
			return ApiResult.ok();
		}
	}

	/**
	 * ??????????????????????????????
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return PageInfo<RealtimeCheckQueryVo>
	 */
	public PageInfo<RealtimeCheckQueryVo> getRealtimePageList(RealtimeCheckQueryParam realtimeCheckQueryParam) {
		// ???????????????????????????????????????????????????
		Long onlineLongTime = DateUtils.getLongTime(DateUtils.parseDate(onlineTime));
		Date createTime = realtimeCheckQueryParam.getCreateTimeFrom();
		Long createLongTime = DateUtils.getLongTime(createTime);
		Date endTime = realtimeCheckQueryParam.getCreateTimeEnd();
		Long endLongTime = DateUtils.getLongTime(endTime);

		// ??????????????????
		Long customerId = ThreadLocalContainer.getCustomerId();
		realtimeCheckQueryParam.setCustomerId(customerId);
		if (realtimeCheckQueryParam.getCreateTimeEnd() != null) {
			realtimeCheckQueryParam.setCreateTimeEnd(DateUtil.endOfDay(realtimeCheckQueryParam.getCreateTimeEnd()));
		}

		return this.queryNewData(realtimeCheckQueryParam);
	}

	/**
	 *  ?????????????????????????????????????????????
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
	 * ?????????????????????????????????????????????
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
	 * ?????????????????????????????????????????????
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return PageInfo<RealtimeCheckQueryVo>
	 */
	private PageInfo<RealtimeCheckQueryVo> queryOldMixNewData(RealtimeCheckQueryParam realtimeCheckQueryParam) {
		Date onlineTimeDate = DateUtils.parseDate(onlineTime);
		// ??????????????????????????????00:00:00??????????????????????????????<=
		Date middleLine = DateUtils.getYesterdayEnd(onlineTimeDate);
		Date originEndTime = realtimeCheckQueryParam.getCreateTimeEnd();

		// ?????????
		realtimeCheckQueryParam.setCreateTimeEnd(DateUtil.endOfDay(middleLine));
		List<RealtimeCheckQueryVo> list = realtimeCheckMapper.getRealtimePageList(realtimeCheckQueryParam);
		list.forEach((emptyCheck) -> {
			emptyCheck.setIsOldData(1);
		});

		// ?????????
		realtimeCheckQueryParam.setCreateTimeFrom(DateUtil.beginOfDay(onlineTimeDate));
		realtimeCheckQueryParam.setCreateTimeEnd(DateUtil.endOfDay(originEndTime));
		List<RealtimeCheckQueryVo> list2 = realtimeCvsFilePathMapper.findList(realtimeCheckQueryParam);
		list2.forEach((emptyCheck)->{
			emptyCheck.setIsOldData(0);
		});
		list2.addAll(list);

		// ??????????????????
		PageInfo<RealtimeCheckQueryVo> info = PageInfoUtils.list2PageInfo(list2, realtimeCheckQueryParam.getPage(), realtimeCheckQueryParam.getSize());
		return info;
	}

	/**
	 * ???????????????????????????????????????
	 * @date 2021/11/15
	 * @param
	 * @return ApiResult<RealtimeCheck>
	 */
	public ApiResult<RealtimeCheckQueryVo> getLatestRealtime() {
		// ????????????????????????????????????????????????????????????????????????
		Long onlineLongTime = DateUtils.getLongTime(DateUtils.parseDate(onlineTime));
		Customer customer = ThreadLocalContainer.getCustomer();
		Long createTime = DateUtils.getLongTime(customer.getCreateTime());

		// ???????????????????????????
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
