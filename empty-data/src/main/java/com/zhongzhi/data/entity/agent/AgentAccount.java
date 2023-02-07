package com.zhongzhi.data.entity.agent;

import com.zhongzhi.data.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 代理商账户表
 * @author xybb
 * @date 2021-11-08
 */
@Data
@NoArgsConstructor
public class AgentAccount extends BaseEntity {

    @ApiModelProperty(value = "所属代理商编号")
    private Long agentId;

    @ApiModelProperty(value = "空号消耗条数（其名下客户消耗日统计累计）")
    private Long emptyConsume;

    @ApiModelProperty(value = "空号充值总计（代理商充值累计）")
    private Long emptyRechargeMoney;

    @ApiModelProperty(value = "实时检测消耗条数")
    private Long realtimeConsume;

    @ApiModelProperty(value = "实时检测充值总计")
    private Long realtimeRechargeMoney;
    
    @ApiModelProperty(value = "国际检测消耗条数")
    private Long internationalConsume;

    @ApiModelProperty(value = "国际检测充值总计")
    private Long internationalRechargeMoney;
    
    @ApiModelProperty(value = "定向通用检测消耗条数")
    private Long directCommonConsume;

    @ApiModelProperty(value = "定向通用检测充值总计")
    private Long directCommonRechargeMoney;
    
    @ApiModelProperty(value = "line定向检测消耗条数")
    private Long lineDirectConsume;

    @ApiModelProperty(value = "line定向检测充值总计")
    private Long lineDirectRechargeMoney;

    @ApiModelProperty(value = "空号充值总条数")
    private Long emptyRechargeNumber;

    @ApiModelProperty(value = "实时检测充值总条数")
    private Long realtimeRechargeNumber;
    
    @ApiModelProperty(value = "国际检测充值总条数")
    private Long internationalRechargeNumber;
    
    @ApiModelProperty(value = "定向通用检测充值总条数")
    private Long directCommonRechargeNumber;
    
    @ApiModelProperty(value = "line定向检测充值总条数")
    private Long lineDirectRechargeNumber;

    @ApiModelProperty(value = "空号余额")
    private Long emptyBalance;

    @ApiModelProperty(value = "实时检测余额")
    private Long realtimeBalance;
    
    @ApiModelProperty(value = "国际检测余额")
    private Long internationalBalance;
    
    @ApiModelProperty(value = "定向通用检测余额")
    private Long directCommonBalance;
    
    @ApiModelProperty(value = "line定向检测余额")
    private Long lineDirectBalance;

    @ApiModelProperty(value = "客户空号充值金额累计")
    private BigDecimal custRechargeMoney;

    @ApiModelProperty(value = "客户空号充值条数累计")
    private Long custRechargeNum;

    @ApiModelProperty(value = "客户实时检测充值金额累计")
    private BigDecimal custRealtimeRechargeMoney;

    @ApiModelProperty(value = "客户实时检测充值条数累计")
    private Long custRealtimeRechargeNum;
    
    @ApiModelProperty(value = "客户国际检测充值金额累计")
    private BigDecimal custInternationalRechargeMoney;

    @ApiModelProperty(value = "客户国际检测充值条数累计")
    private Long custInternationalRechargeNum;
    
    @ApiModelProperty(value = "客户定向通用检测充值金额累计")
    private BigDecimal custDirectCommonRechargeMoney;

    @ApiModelProperty(value = "客户定向通用检测充值条数累计")
    private Long custDirectCommonRechargeNum;
    
    @ApiModelProperty(value = "客户line定向检测充值金额累计")
    private BigDecimal custLineDirectRechargeMoney;

    @ApiModelProperty(value = "客户line定向检测充值条数累计")
    private Long custLineDirectRechargeNum;
}
