package com.zhongzhi.data.service.agent;

import cn.hutool.core.collection.CollectionUtil;

import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.entity.agent.Agent;
import com.zhongzhi.data.entity.agent.AgentAccount;
import com.zhongzhi.data.entity.agent.AgentRechargeTotalInfo;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.mapper.agent.AgentMapper;
import com.zhongzhi.data.mapper.agent.AgentRechargeMapper;
import com.zhongzhi.data.mapper.customer.CustomerRechargeMapper;
import com.zhongzhi.data.mapper.customer.CustomerRefundMapper;
import com.zhongzhi.data.util.RequestUtil;
import com.zhongzhi.data.vo.AgentQueryVo;
import com.zhongzhi.data.vo.customer.CustomerRechargeQueryVo;
import com.zhongzhi.data.vo.customer.CustomerRefundQueryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <pre>
 * 代理商管理 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Service
public class AgentService {

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    /**
     * 默认号码魔方地址
     * @date 2021/11/24
     * @param null
     * @return null
     */
    public static final String DEFAULT_CUBE_PATH = "%E5%8F%B7%E7%A0%81%E9%AD%94%E6%96%B9.rar";
    
    public static final String DEFAULT_INTERNATIONAL_CUBE_PATH = "%E5%9B%BD%E9%99%85%E5%8F%B7%E7%A0%81%E9%AD%94%E6%96%B9.rar";

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private AgentRechargeMapper agentRechargeMapper;

    @Autowired
    private CustomerRechargeMapper customerRechargeMapper;

    @Autowired
    private CustomerRefundMapper customerRefundMapper;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private AgentAccountService agentAccountService;

    /**
     * 代理商-查找（通过id）
     * @date 2021/10/29
     * @param id
     * @return com.zhongzhi.data.entity.agent.Agent
     */
    public Agent findById(Long id) {
        return agentMapper.findById(id);
    }

    public Long getAgentEmptyBalance(Long agentId) {
        AgentAccount agentAccount = agentAccountService.findByAgentId(agentId);
        if (agentAccount == null || agentAccount.getEmptyBalance()==null) {
            logger.error("代理商id:{}，查询代理商账户表失败或账户空号余额为null。", agentId);
            return 0L;
        } else {
            return agentAccount.getEmptyBalance();
        }
    }

    public Long getAgentRealtimeBalance(Long agentId) {
        AgentAccount agentAccount = agentAccountService.findByAgentId(agentId);
        if (agentAccount == null || agentAccount.getRealtimeBalance()==null) {
            logger.error("代理商id:{}，查询代理商账户表失败或账户实时检测余额为null。", agentId);
            return 0L;
        } else {
            return agentAccount.getRealtimeBalance();
        }
    }
    
    public Long getAgentInternationalBalance(Long agentId) {
        AgentAccount agentAccount = agentAccountService.findByAgentId(agentId);
        if (agentAccount == null || agentAccount.getInternationalBalance()==null) {
            logger.error("代理商id:{}，查询代理商账户表失败或账户国际余额为null。", agentId);
            return 0L;
        } else {
            return agentAccount.getInternationalBalance();
        }
    }
    
    public Long getAgentLineDirectBalance(Long agentId) {
        AgentAccount agentAccount = agentAccountService.findByAgentId(agentId);
        if (agentAccount == null || agentAccount.getInternationalBalance()==null) {
            logger.error("代理商id:{}，查询代理商账户表失败或账户line定向余额为null。", agentId);
            return 0L;
        } else {
            return agentAccount.getLineDirectBalance();
        }
    }
    
    public Long getAgentDirectCommonBalance(Long agentId) {
        AgentAccount agentAccount = agentAccountService.findByAgentId(agentId);
        if (agentAccount == null || agentAccount.getInternationalBalance()==null) {
            logger.error("代理商id:{}，查询代理商账户表失败或账户定向通用余额为null。", agentId);
            return 0L;
        } else {
            return agentAccount.getDirectCommonBalance();
        }
    }

    public void addAgentEmptyAccountInfo(List<AgentQueryVo> agentList) {
        emptyAgentAccountInit(agentList);

        List<Long> agentIds = agentList.stream()
                .map(AgentQueryVo::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(agentIds)) {
            logger.error("代理商列表中所有代理商Id为空");
            return;
        }
        if (agentIds.size() != agentList.size()) {
            logger.error("代理商列表中部分代理商Id为null");
        }

        // 通过代理商Id查询代理商充值汇总信息
        List<AgentRechargeTotalInfo> rechargeInfos = agentRechargeMapper.selectAgentRechargeInfo(agentIds, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtil.isNotEmpty(rechargeInfos)) {
            // 充值总金额和充值总条数数据绑定在代理商中
            agentList.forEach(a ->
                    {
                        Optional<AgentRechargeTotalInfo> rechargeInfo =
                                rechargeInfos.stream().filter(r -> a.getId().equals(r.getAgentId())).findFirst();
                        if (rechargeInfo.isPresent()) {
                            a.setRechargeNumberTotal(rechargeInfo.get().getRechargeNumberTotal());
                            a.setRemainNumberTotal(a.getRechargeNumberTotal());
                            a.setPaymentAmountTotal(rechargeInfo.get().getPaymentAmountTotal());
                        } else {
                            a.setRemainNumberTotal(0L);
                        }
                    }
            );
        }

        // 通过代理商Id查询其为客户充值总信息
        List<CustomerRechargeQueryVo> consumeInfos =
                customerRechargeMapper.countCustomerRechargeInfoByAgentIds(agentIds, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtil.isNotEmpty(consumeInfos)) {
            // 剩余条数处理
            agentList.forEach(a ->
                    {
                        Optional<CustomerRechargeQueryVo> consumeTotalInfo =
                                consumeInfos.stream().filter(c -> a.getId().equals(c.getAgentId())).findFirst();
                        if (consumeTotalInfo.isPresent() && consumeTotalInfo.get().getRechargeNumber() != null) {
                            a.setEmptyCustomerConsumeTotalCount(consumeTotalInfo.get().getRechargeNumber());
                            a.setRemainNumberTotal(a.getRemainNumberTotal() - consumeTotalInfo.get().getRechargeNumber());
                        }
                    }
            );
        }

        // 通过代理商Id查询其为客户退款总信息
        List<CustomerRefundQueryVo> refundInfos = customerRefundMapper.countCustomerRefundInfoByAgentIds(agentIds, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtil.isNotEmpty(refundInfos)) {
            // 剩余条数处理
            agentList.forEach(a ->
                    {
                        Optional<CustomerRefundQueryVo> refundTotalInfo =
                                refundInfos.stream().filter(c -> a.getId().equals(c.getAgentId())).findFirst();
                        if (refundTotalInfo.isPresent() && refundTotalInfo.get().getRefundNumber() != null) {
                            a.setEmptyCustomerRefundTotalCount(refundTotalInfo.get().getRefundNumber());
                            a.setEmptyCustomerRefundTotalPay(refundTotalInfo.get().getRefundAmount());
                            a.setRemainNumberTotal(a.getRemainNumberTotal() + refundTotalInfo.get().getRefundNumber());
                        }
                        if (a.getRemainNumberTotal() < 0) {
                            a.setRemainNumberTotal(0);
                        }
                    }
            );
        }
    }

    public void addAgentRealtimeAccountInfo(List<AgentQueryVo> agentList) {
        realtimeAgentAccountInit(agentList);

        List<Long> agentIds = agentList.stream()
                .map(AgentQueryVo::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(agentIds)) {
            logger.error("代理商列表中所有代理商Id为空");
            return;
        }
        if (agentIds.size() != agentList.size()) {
            logger.error("代理商列表中部分代理商Id为null");
        }

        // 通过代理商Id查询代理商充值汇总信息
        List<AgentRechargeTotalInfo> rechargeInfos = agentRechargeMapper.selectAgentRechargeInfo(agentIds, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtil.isNotEmpty(rechargeInfos)) {
            // 充值总金额和充值总条数数据绑定在代理商中
            agentList.forEach(a ->
                    {
                        Optional<AgentRechargeTotalInfo> rechargeInfo =
                                rechargeInfos.stream().filter(r -> a.getId().equals(r.getAgentId())).findFirst();
                        if (rechargeInfo.isPresent()) {
                            a.setRealtimeAgentRechargeTotalCount(rechargeInfo.get().getRechargeNumberTotal());
                            a.setRealtimeAgentBalance(a.getRealtimeAgentRechargeTotalCount());
                            a.setRealtimeAgentRechargeTotalPay(rechargeInfo.get().getPaymentAmountTotal());
                        } else {
                            a.setRealtimeAgentBalance(0L);
                        }
                    }
            );
        }


        // 通过代理商Id查询其为客户充值总信息
        List<CustomerRechargeQueryVo> consumeInfos =
                customerRechargeMapper.countCustomerRechargeInfoByAgentIds(agentIds, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtil.isNotEmpty(consumeInfos)) {
            // 剩余条数处理
            agentList.forEach(a ->
                    {
                        Optional<CustomerRechargeQueryVo> consumeTotalInfo =
                                consumeInfos.stream().filter(c -> a.getId().equals(c.getAgentId())).findFirst();
                        if (consumeTotalInfo.isPresent() && consumeTotalInfo.get().getRechargeNumber() != null) {
                            a.setRealtimeCustomerConsumeTotalCount(consumeTotalInfo.get().getRechargeNumber());
                            a.setRealtimeAgentBalance(a.getRealtimeAgentBalance() - consumeTotalInfo.get().getRechargeNumber());
                        }
                    }
            );
        }

        // 通过代理商Id查询其为客户退款总信息
        List<CustomerRefundQueryVo> refundInfos = customerRefundMapper.countCustomerRefundInfoByAgentIds(agentIds, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtil.isNotEmpty(refundInfos)) {
            // 剩余条数处理
            agentList.forEach(a ->
                    {
                        Optional<CustomerRefundQueryVo> refundTotalInfo =
                                refundInfos.stream().filter(c -> a.getId().equals(c.getAgentId())).findFirst();
                        if (refundTotalInfo.isPresent() && refundTotalInfo.get().getRefundNumber() != null) {
                            a.setRealtimeCustomerRefundTotalCount(refundTotalInfo.get().getRefundNumber());
                            a.setRealtimeCustomerRefundTotalPay(refundTotalInfo.get().getRefundAmount());
                            a.setRealtimeAgentBalance(a.getRealtimeAgentBalance() + refundTotalInfo.get().getRefundNumber());
                        }
                        if (a.getRealtimeAgentBalance() < 0) {
                            a.setRealtimeAgentBalance(0);
                        }
                    }
            );
        }
    }

    private void emptyAgentAccountInit(List<AgentQueryVo> agentList) {
        if (CollectionUtils.isEmpty(agentList)) {
            return;
        }
        agentList.forEach(agent -> {
            agent.setPaymentAmountTotal(0)
                    .setEmptyCustomerRefundTotalPay("0")
                    .setRechargeNumberTotal(0)
                    .setEmptyCustomerRefundTotalCount(0)
                    .setEmptyCustomerConsumeTotalCount(0)
                    .setRemainNumberTotal(0)
            ;
        });
    }

    private void realtimeAgentAccountInit(List<AgentQueryVo> agentList) {
        if (CollectionUtils.isEmpty(agentList)) {
            return;
        }
        agentList.forEach(agent -> {
            agent.setRealtimeAgentRechargeTotalPay(0)
                    .setRealtimeCustomerRefundTotalPay("0")
                    .setRealtimeAgentRechargeTotalCount(0)
                    .setRealtimeCustomerRefundTotalCount(0)
                    .setRealtimeCustomerConsumeTotalCount(0)
                    .setRealtimeAgentBalance(0)
            ;
        });
    }

    /**
     * 获取代理商号码魔方地址
     * @date 2021/11/17
     * @param
     * @return String
     */
    public String getMobileCubePath(String fileType) {
        String domain = RequestUtil.getRequest().getHeader("domain");
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings == null) {
            throw new BusinessException(ApiCode.FAIL.getCode(), "代理商状态异常，请联系客服进行处理，谢谢！");
        }
        Agent agent = this.findById(agentSettings.getAgentId());
        if (agent != null) {
            return Constant.NATIONAL_CUBE_FILE_TYPE.equals(fileType) ? agent.getMobileCubePath() : agent.getInternationalMobileCubePath();
        } else {
            return Constant.NATIONAL_CUBE_FILE_TYPE.equals(fileType) ? DEFAULT_CUBE_PATH : DEFAULT_INTERNATIONAL_CUBE_PATH;
        }
    }
}
