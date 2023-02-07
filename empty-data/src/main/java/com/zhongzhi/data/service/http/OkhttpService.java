package com.zhongzhi.data.service.http;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class OkhttpService extends BaseOkHttpService{	
	
	public String post(String url,Map<String,Object> paramMap){       
        Request.Builder rBuilder = new Request.Builder();
        
        FormBody.Builder builder = new FormBody.Builder();
        for(String key:paramMap.keySet()) {
        	builder.add(key, paramMap.get(key).toString());
        }
        
        RequestBody body = builder.build();
        Request requestPost = rBuilder.url(url).post(body).build();
        long startTime1 = System.currentTimeMillis();
        try (Response res = client.newCall(requestPost).execute()) {
            if (res.isSuccessful()) {
            	log.info("调用下游接口成功，url:{},param:{},useTime:{}",url,JSON.toJSONString(paramMap),System.currentTimeMillis()-startTime1);
            	return res.body().string();
            }
        } catch (Exception e) {
        	log.info("调用下游接口异常，url:{},param:{},info:",url,JSON.toJSONString(paramMap),e);
        }
        
        return null;
    }
	
	public String postXml(String url,String xmlStr){        
        RequestBody body = RequestBody.create(MediaType.parse("application/xml"), xmlStr);
        Request requestPost = new Request.Builder().url(url).post(body).build();
        long startTime1 = System.currentTimeMillis();
        try (Response res = client.newCall(requestPost).execute()) {
            if (res.isSuccessful()) {
            	log.info("调用下游接口成功，url:{},param:{},useTime:{}",url,xmlStr,System.currentTimeMillis()-startTime1);
            	return res.body().string();
            }
        } catch (Exception e) {
        	log.info("调用下游接口异常，url:{},param:{},info:",url,xmlStr,e);
        }
        
        return null;
    }
}
