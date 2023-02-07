package com.zhongzhi.data.entity.customer;

import com.zhongzhi.data.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 客户余额实体类
 * @author xybb
 * @date 2021-11-02
 */
@Data
@NoArgsConstructor
public class CustomerBalance extends BaseEntity {


    @ApiModelProperty(value = "客户id")
    private Long customerId;

    @ApiModelProperty(value = "号检测余额，单位：条")
    private Long emptyCount;

    @ApiModelProperty(value = "实时检测余额，单位：条")
    private Long realtimeCount;
    
    @ApiModelProperty(value = "国际号码检测余额，单位：条")
    private Long internationalCount = 0L;
    
    @ApiModelProperty(value = "定向通用检测余额，单位：条")
    private Long directCommonCount = 0L;
    
    @ApiModelProperty(value = "line定向检测余额，单位：条")
    private Long lineDirectCount = 0L;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "空号充值条数，单位：条")
    private Long emptyRechargeNum;

    @ApiModelProperty(value = "空号充值金额，单位：条")
    private BigDecimal emptyRechargeMoney;

    @ApiModelProperty(value = "实时检测充值条数，单位：条")
    private Long realtimeRechargeNum;

    @ApiModelProperty(value = "实时检测充值金额，单位：条")
    private BigDecimal realtimeRechargeMoney;

    @ApiModelProperty(value = "国际号码检测充值条数，单位：条")
    private Long internationalRechargeNum;

    @ApiModelProperty(value = "国际号码检测充值金额，单位：条")
    private BigDecimal internationalRechargeMoney;
    
    @ApiModelProperty(value = "定向通用检测充值条数，单位：条")
    private Long directCommonRechargeNum;

    @ApiModelProperty(value = "定向通用检测充值金额，单位：条")
    private BigDecimal directCommonRechargeMoney;

    @ApiModelProperty(value = "line定向检测充值条数，单位：条")
    private Long lineDirectRechargeNum;

    @ApiModelProperty(value = "line定向检测充值金额，单位：条")
    private BigDecimal lineDirectRechargeMoney;
}
