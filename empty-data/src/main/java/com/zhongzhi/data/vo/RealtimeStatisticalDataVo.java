package com.zhongzhi.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 客户号码检测统计信息
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "客户号码检测统计信息", description = "客户号码检测统计,今日消耗数，今日活跃数，本月消耗数，本月活跃数")
public class RealtimeStatisticalDataVo {

    @ApiModelProperty(value = "客户id")
    private long customerId;

    @ApiModelProperty(value = "今日消费总条数")
    private long todayConsumeTotal;

    @ApiModelProperty(value = "昨日消费总条数")
    private long yesterdayConsumeTotal;

    @ApiModelProperty(value = "同比昨日消费总条数百分比")
    private String yesterdayConsumeTotalPercentage;

    public String getYesterdayConsumeTotalPercentage() {

        return computePercentage(yesterdayConsumeTotal, todayConsumeTotal);
    }

    /**
     * 计算同比百分比公式：（今日-昨日）/ 昨日
     *
     * @param old
     * @param current
     * @return
     */
    private String computePercentage(long old, long current) {
        long div = old;
        if (old == 0) {
            div = 1;
        }
        double count = BigDecimal.valueOf(current).subtract(BigDecimal.valueOf(old)).divide(BigDecimal.valueOf(div), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Math.abs(count) > 9.99) {
            if (count > 0) {
                return "999%";
            } else {
                return "-999%";
            }
        } else {
            java.text.DecimalFormat df = new java.text.DecimalFormat("##%");//传入格式模板
            return df.format(count);
        }
    }
}
