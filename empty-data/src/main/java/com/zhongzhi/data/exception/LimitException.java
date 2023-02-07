package com.zhongzhi.data.exception;

/**
 * 自定义限流 异常
 *
 * @author techie
 * @since 2021/05/21
 */
public class LimitException extends RuntimeException {

    /**
     * 错误code
     */
    private Integer errorCode;

    /**
     * 异常信息
     */
    private static String message;

    public LimitException(String message) {
        super(message);
    }

    public LimitException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}