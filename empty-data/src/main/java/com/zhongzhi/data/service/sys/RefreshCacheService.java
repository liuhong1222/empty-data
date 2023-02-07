package com.zhongzhi.data.service.sys;

import com.alibaba.fastjson.JSONObject;
import com.zhongzhi.data.constants.CacheConstant;
import com.zhongzhi.data.redis.RedisClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 刷新缓存实现类
 * @author liuh
 * @date 2021年11月10日
 */
@Slf4j
@Service
public class RefreshCacheService {

    @Autowired
    private RedisClient redisClient;

	public void customerInfoRefresh(Long customerId) {
		JSONObject json = new JSONObject();
		json.put("option", CacheConstant.CUSTOMER_CACHE);
		json.put("customerId", customerId);
		publish(json);
	}

    private void publish(JSONObject json) {
    	redisClient.publish(CacheConstant.CACHE_REFRESH_CHANNEL, json.toJSONString());
    }
}
