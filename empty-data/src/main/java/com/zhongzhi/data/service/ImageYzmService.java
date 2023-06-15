package com.zhongzhi.data.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.util.HttpClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 图文验证码实现
 * @author liuh
 * @date 2023年6月15日
 */
@Slf4j
@Service
public class ImageYzmService {

	@Value("${http.ws.iyzm.url}")
	private String wsIyzmUrl;
	
	@Value("${http.ws.iyzm.appid}")
	private String wsIyzmAppid;
	
	@Value("${http.ws.iyzm.appkey}")
	private String wsIyzmAppkey;
	
	@Value("${http.ws.iyzm.AppSecretKey}")
	private String wsIyzmAppSecretKey;
	
	@Value("${http.ws.iyzm.CaptchaAppId}")
	private String wsIyzmCaptchaAppId;
	
	public ApiResult invokeImageYzm(String ip,String randStr,String ticket) {
		try {
			Map<String,Object> param = new HashMap<>();
			param.put("AppSecretKey",wsIyzmAppSecretKey);
			param.put("appId",wsIyzmAppid);
			param.put("appKey",wsIyzmAppkey);
			param.put("CaptchaAppId",wsIyzmCaptchaAppId);
			param.put("RendStr",randStr);
			param.put("Ticket",ticket);
			param.put("IP",ip);
			
			String resultString = HttpClient.post(wsIyzmUrl, param);
			JSONObject resultObject = JSONObject.parseObject(resultString);
			if (resultObject == null || resultObject.get("data") == null){
				return ApiResult.fail("业务异常，滑块验证码服务器无响应");
			}
			
			JSONObject dataObject = JSONObject.parseObject(resultObject.get("data").toString());
			Object captchaCode = Optional.ofNullable(dataObject.get("CaptchaCode")).orElse("-1");
			Integer code = Integer.valueOf(captchaCode.toString());
			//CaptchaCode 为 1 时验证通过
			if (code != 1){
				log.error("万数图文验证码接口调用失败，param:{},result:{}",JSON.toJSONString(param),resultString);
				return ApiResult.fail(dataObject.get("CaptchaMsg").toString());
			}
			
			log.info("万数图文验证码接口调用成功，result:{}",resultString);
			return ApiResult.ok();
		} catch (Exception e) {
			log.error("万数图文验证码接口调用异常，ip:{},randStr:{},ticket:{},info:",ip,randStr,ticket,e);
			return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION);
		}		
	}
}
