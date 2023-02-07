package com.zhongzhi.data.entity.customer;

import org.apache.commons.lang3.StringUtils;

/**
 * 客户退款汇总信息
 */
public class CustomerRefundTotalInfo {

    /**
     * 客户Id
     */
    private long customerId;

    /**
     * 退款总条数
     */
    private Long refundNumberTotal;

    private String refundTotalPay;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRefundNumberTotal() {
        if (refundNumberTotal == null) {
            return 0L;
        }
        return refundNumberTotal;
    }

    public void setRefundNumberTotal(Long refundNumberTotal) {
        this.refundNumberTotal = refundNumberTotal;
    }

    public String getRefundTotalPay() {
        if (StringUtils.isBlank(refundTotalPay)) {
            return "0";
        }
        return refundTotalPay;
    }

    public void setRefundTotalPay(String refundTotalPay) {
        this.refundTotalPay = refundTotalPay;
    }
}
