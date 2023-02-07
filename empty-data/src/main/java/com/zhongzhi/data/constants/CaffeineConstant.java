package com.zhongzhi.data.constants;

/**
 * 本地缓存常量类
 * @author liuh
 * @date 2021年10月26日
 */
public class CaffeineConstant {

	/**
	 * 缓存前缀
	 */
	public final static String CACHE_PREFIX = "ec_";
	
	/**
	 * 缓存版本号
	 */
	public final static String CACHE_VERSION = "v1_";
	
	/**
	 * api账号信息缓存
	 */
	public final static String API_SETTINGS_INFO = CACHE_PREFIX + CACHE_VERSION + "api_settings";
}
