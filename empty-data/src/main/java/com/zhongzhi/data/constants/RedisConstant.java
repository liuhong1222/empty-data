package com.zhongzhi.data.constants;

/**
 * redis key常量
 * @author liuh
 * @date 2021年3月12日
 */
public class RedisConstant {

	/**
	 * token缓存前缀
	 */
	public final static String LOGIN_TOKEN_PREFIX = "login:token:";

	/**
	 * redis set成功的字符
	 */
	public final static String REDIS_SET_SUCCESS = "OK";

	/**
	 * 限流出现超限的key
	 */
	public final static String CURRENT_LIMIT_KEY = "current:limit:key:%s";

	/**
	 * 登录短信验证码前缀
	 */
	public final static String LOGIN_SMS_CODE_PREFIX = "login:sms:";

	/**
	 * 修改手机号短信验证码前缀
	 */
	public final static String MOBILE_SMS_CODE_PREFIX = "mobile:sms:";

	/**
	 * 忘记密码校验令牌
	 */
	public final static String FORGET_VERIFY_TOKEN = "forget:verify:token:";

	/**
	 * 登录短信验证码发送过前缀
	 */
	public final static String LOGIN_SMS_CODE_FLAG_PREFIX = "login:sms:flag:";

	/**
	 * 修改手机号短信验证码发送过前缀
	 */
	public final static String MOBILE_SMS_CODE_FLAG_PREFIX = "mobile:sms:flag:";

	public final static String CUSTOMER_VO_KEY = "customer:vo:key:";

	public final static String CUSTOMER_EXT_KEY = "customer:ext:key:";

	public final static String CUSTOMER_EMPTY_ACCOUNT_BALANCE = "customer:empty:account:balance:%s";

	public final static String CUSTOMER_EMPTY_ACCOUNT_CONSUME = "customer:empty:account:consume:%s";

	/**
	 * 号码匹配redis bit Key
	 */
	public final static String PHONE_MATCHER_KEY = "phone_matcher_key_";
	
	/**
	 * 合并文件锁key
	 */
	public final static String UPLOAD_UNION_CHUNKS_KEY = "file:uulk:%s:%s";
	
	/**
	 * 文件md5缓存key
	 */
	public final static String FILE_MD5_CACHE_KEY = "file:fmck:%s:%s";

	/**
	 * 活跃用户Key
	 */
	public final static String ACTIVE_KEY = "bloom_active";

	/**
	 * 沉默用户Key
	 */
	public final static String SILENT_KEY = "bloom_silent";

	/**
	 * 空号用户Key
	 */
	public final static String EMPTY_KEY = "bloom_empty";

	/**
	 * 风险用户Key
	 */
	public final static String RISK_KEY = "bloom_risk";

	/**
	 * key项目前缀
	 */
	public final static String CACHE_PREFIX = "eo:";

	/**
	 * key模块前缀
	 */
	public final static String CACHE_VERSION = "balance:";

	/**
	 * 空号检测余额key
	 */
	public final static String EMPTY_BALANCE_KEY = CACHE_PREFIX + CACHE_VERSION + "empty:";

	/**
	 * 实时检测余额key
	 */
	public final static String REALTIME_BALANCE_KEY = CACHE_PREFIX + CACHE_VERSION + "realtime:";
	
	/**
	 * 国际检测余额key
	 */
	public final static String INTERNATIONAL_BALANCE_KEY = CACHE_PREFIX + CACHE_VERSION + "international:";
	
	/**
	 * 定向通用检测余额key
	 */
	public final static String DIRECT_COMMON_BALANCE_KEY = CACHE_PREFIX + CACHE_VERSION + "directCommon:";
	
	/**
	 * line定向检测余额key
	 */
	public final static String LINE_DIRECT_BALANCE_KEY = CACHE_PREFIX + CACHE_VERSION + "lineDirect:";

	/**
	 * 客户是否可以进行检测缓存前缀
	 */
	public final static String CUSTOMER_IS_PERMISSION = "customer:permission:";
}
