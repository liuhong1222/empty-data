package com.zhongzhi.data.constants;

/**
 * 常量类
 * @author xybb
 * @date 2021-10-28
 */
public class Constant {
    /**
     * 登录token校验时需去掉的字符
     */
    public final static String TOKEN_NO_STRING = "0";

    /**
     * 一天时间。单位：秒
     */
    public static final int ONE_DAY = 86400;

    /**
     * 6小时。单位：秒
     */
    public static final int SIX_HOUR = 21600;

    /**
     * 5小时。单位：秒
     */
    public static final int FIVE_HOUR = 18000;

    /**
     * 1分钟。单位：秒
     */
    public static final int ONE_MINUTES = 60;

    /**
     * 2分钟。单位：秒
     */
    public static final int TWO_MINUTES = 120;

    /**
     * 30分钟。单位：秒
     */
    public static final int THIRTY_MINUTES = 1800;

    /**
     * 本机地址IP
     */
    public static final String LOCALHOST_IP = "127.0.0.1";
    /**
     * 本机地址名称
     */
    public static final String LOCALHOST_IP_NAME = "本机地址";
    /**
     * 局域网IP
     */
    public static final String LAN_IP = "192.168";

    /**
     * 局域网名称
     */
    public static final String LAN_IP_NAME = "局域网";

    /**
     * 用户浏览器代理
     */
    public static final String USER_AGENT = "User-Agent";

    /**
     * referer url
     */
    public static final String REFERER = "Referer";

    /**
     * 登录token
     */
    public static final String JWT_DEFAULT_TOKEN_NAME = "token";

    /**
     * JWT Token默认密钥
     */
    public static final String JWT_DEFAULT_SECRET = "666666";

    /**
     * JWT 默认过期时间，3600L，单位秒
     */
    public static final Long JWT_DEFAULT_EXPIRE_SECOND = 3600L;

    /**
     * 代理商网站登陆用户token信息key
     */
    public static final String FRONT_LOGIN_TOKEN = "front:login:token:";

    /**
     * 代理商网站登陆用户信息key
     */
    public static final String FRONT_LOGIN_USER = "front:login:user:";

    /**
     * 限流出现超限的key
     */
    public static final String CURRENT_LIMIT_KEY = "current:limit:key:";
    
    /**
     * 空号文件最小行数
     */
    public static final Integer EMPTY_MIN_LINE_NUM = 3000;
    
    /**
     * 实时文件最小行数
     */
    public static final Integer REALTIME_MIN_LINE_NUM = 0;
    
    /**
     * 国际文件最小行数
     */
    public static final Integer INTERNATIONAL_MIN_LINE_NUM = 1999;
    
    /**
     * 文件最大行数
     */
    public static final Integer MAX_LINE_NUM = 3000000;
    
    /**
	 * 空号检测检测中的code
	 */
	public final static String FILE_TESTING_CODE = "000000";
	
	/**
	 * 空号检测检测失败的code
	 */
	public final static String FILE_TEST_FAILED_CODE = "999999";

    /**
     * 客户有权限进行检测。0：有，1：无
     */
    public static final Integer UNPERMIT = 0;

    /**
     * 客户有权限进行检测。0：有，1：无
     */
    public static final Integer PERMIT = 1;

    /**
     * 微信扫码付成功code
     */
    public static final String WEIXINPAY_SUCCESS_CODE = "SUCCESS";
    
    /**
     * 国内号码魔方文件类型
     */
    public static final String NATIONAL_CUBE_FILE_TYPE = "national";
}
