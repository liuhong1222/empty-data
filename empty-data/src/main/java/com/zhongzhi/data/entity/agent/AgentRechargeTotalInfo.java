package com.zhongzhi.data.entity.agent;

/**
 * 经销商充值汇总信息
 */
public class AgentRechargeTotalInfo {
    /**
     * 代理商Id
     */
    private long agentId;

    /**
     * 充值总金额
     */
    private Long paymentAmountTotal;

    /**
     * 充值总条数
     */
    private Long rechargeNumberTotal;

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public long getPaymentAmountTotal() {
        if (paymentAmountTotal == null) {
            return 0L;
        }
        return paymentAmountTotal;
    }

    public void setPaymentAmountTotal(Long paymentAmountTotal) {
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