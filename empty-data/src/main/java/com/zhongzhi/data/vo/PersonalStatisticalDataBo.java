package com.zhongzhi.data.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户号码检测统计信息
 */
@Getter
@Setter
public class PersonalStatisticalDataBo {

    /**
     * 客户Id
     */
    private long customerId;

    /**
     * 消费总条数
     */
    private long consumeTotal;

    /**
     * 活跃号码总条数
     */
    private long activeTotal;


}
