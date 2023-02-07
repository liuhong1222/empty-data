/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */

package com.zhongzhi.data.util;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Description:Jackson工具类 User: liutao Date: 2018-02-21 Time: 16:08
 */
public final class JacksonUtil {
	
	/**
	 * logger.
	 */
    private static final Logger logger     = LoggerFactory.getLogger(JacksonUtil.class);
    /**
     * gson.
     */
    private static Gson         gson;
    /**
     * json parser.
     */
    private static JsonParser   jsonParser = new JsonParser();

    /**
     * static block
     */
    static {
    	gson = new GsonBuilder()  
    			  .setDateFormat("yyyy-MM-dd HH:mm:ss")  
    			  .create();    	
    }

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType) {
        return exec(() -> gson.fromJson(jsonStr, valueType));
    }


    public static <T> T readValue(String jsonStr, Type type) {
        return exec(() -> gson.fromJson(jsonStr, type));
    }


    /**
     * JSONElement 转自定义TYPE的任意对象
     */
    public static <T> T readValue(JsonElement json, Type type) {
        return exec(() -> gson.fromJson(json, type));
    }


    public static <T> T readValue(JsonElement json, Class<T> valueType) {
        return exec(() -> gson.fromJson(json, valueType));
    }

    /**
     * 把Object转换为json字符串
     */
    public static String toJson(Object object) {
        return exec(() -> gson.toJson(object));
    }


    /**
     * 把Object转换为json字符串
     */
    public static String toJson(Integer[] ints) {
        return exec(() -> gson.toJson(ints));
    }


    /**
     * json string to JsonNode
     *
     * @param jsonStr
     * @return JsonNode
     */
    public static JsonObject toJsonNode(String jsonStr) {
        return exec(() -> jsonParser.parse(jsonStr).getAsJsonObject());
    }


    private static <T> T exec(Function<T> function) {
        try {
            return function.apply();
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @FunctionalInterface
    private static interface Function<T> {
        T apply();
    }


}
