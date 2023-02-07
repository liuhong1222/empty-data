package com.zhongzhi.data.entity.customer;

public class CustomerGiftTotalInfo {
    /**
     * 客户Id
     */
    private long customerId;

    /**
     * 赠送总条数
     */
    private Long totalNumber;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Long getTotalNumber() {
        if (totalNumber == null) {
            totalNumber = 0L;
        }
        return totalNumber;
    }

    public void setTotalNumber(Long totalNumber) {
        this.totalNumber = totalNumber;
    }
}
