package com.zhongzhi.data.constants;

/**
 * rediskey 常量类
 * @author liuh
 * @date 2021年10月27日
 */
public class BalanceRedisKeyConstant {

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
	 * 空号检测冻结金额key
	 */
	public final static String EMPTY_FREEZE_AMOUNT_KEY = CACHE_PREFIX + CACHE_VERSION + "efak:";
	
	/**
	 * 空号检测实际扣款金额key
	 */
	public final static String EMPTY_REAL_DEDUCT_FEE_KEY = CACHE_PREFIX + CACHE_VERSION + "erdfk:";
	
	/**
	 * 实时检测余额key
	 */
	public final static String REALTIME_BALANCE_KEY = CACHE_PREFIX + CACHE_VERSION + "realtime:";
	
	/**
	 * 实时检测冻结金额key
	 */
	public final static String REALTIME_FREEZE_AMOUNT_KEY = CACHE_PREFIX + CACHE_VERSION + "rfak:";
	
	/**
	 * 实时检测实际扣款金额key
	 */
	public final static String REALTIME_REAL_DEDUCT_FEE_KEY = CACHE_PREFIX + CACHE_VERSION + "rrdfk:";
	
	/**
	 * 限流出现超限的key
	 */
	public final static String CURRENT_LIMIT_KEY = CACHE_PREFIX + CACHE_VERSION + "clk:";
}
