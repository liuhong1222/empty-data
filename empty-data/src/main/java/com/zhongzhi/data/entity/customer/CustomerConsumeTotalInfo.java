package com.zhongzhi.data.entity.customer;

/**
 * 客户消费汇总信息
 */
public class CustomerConsumeTotalInfo {

    /**
     * 客户Id
     */
    private long customerId;

    /**
     * 消费总条数
     */
    private Long consumeNumberTotal;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public long getConsumeNumberTotal() {
        if (consumeNumberTotal == null) {
            return 0L;
        }
        return consumeNumberTotal;
    }

    public void setConsumeNumberTotal(Long consumeNumberTotal) {
        this.consumeNumberTotal = consumeNumberTotal;
    }
}
