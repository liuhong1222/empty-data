package com.zhongzhi.data.entity.customer;

import org.apache.commons.lang3.StringUtils;

public class CustomerRechargeTotalInfo {
    /**
     * 客户Id
     */
    private long customerId;

    /**
     * 充值总金额
     */
    private String paymentAmountTotal;

    /**
     * 充值总条数
     */
    private Long rechargeNumberTotal;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getPaymentAmountTotal() {
        if (StringUtils.isBlank(paymentAmountTotal)) {
            return "0";
        }
        return paymentAmountTotal;
    }

    public void setPaymentAmountTotal(String paymentAmountTotal) {
        this.paymentAmountTotal = paymentAmountTotal;
    }

    public long getRechargeNumberTotal() {
        if (rechargeNumberTotal == null) {
            return 0L;
        }
        return rechargeNumberTotal;
    }

    public void setRechargeNumberTotal(Long rechargeNumberTotal) {
        this.rechargeNumberTotal = rechargeNumberTotal;
    }
}
