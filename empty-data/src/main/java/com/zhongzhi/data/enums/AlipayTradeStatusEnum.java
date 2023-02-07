package com.zhongzhi.data.enums;

/**
 * 支付宝交易状态枚举
 *
 * @author rivers
 * @since 2020-2-18
 **/
public enum AlipayTradeStatusEnum implements BaseEnum {
    WAIT_BUYER_PAY(0, "WAIT_BUYER_PAY"),
    TRADE_CLOSED(1, "TRADE_CLOSED"),
    TRADE_SUCCESS(2, "TRADE_SUCCESS"),
    TRADE_FINISHED(3, "TRADE_FINISHED"),
    WEIXIN_TRADE_FAIL(1, "FAIL"),
    WEIXIN_TRADE_SUCCESS(2, "SUCCESS"),
    ;

    private Integer code;
    private String desc;

    AlipayTradeStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static Integer getCode(String desc) {
    	AlipayTradeStatusEnum[] alipayTradeStatusEnums = AlipayTradeStatusEnum.values();
		for (AlipayTradeStatusEnum enums : alipayTradeStatusEnums) {
			if (enums.getDesc().equals(desc)) {
				return enums.getCode();
			}
		}
		return null;
	}

    public void setCode(Integer code) {
		this.code = code;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
