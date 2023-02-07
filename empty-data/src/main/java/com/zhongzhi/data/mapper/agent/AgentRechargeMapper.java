package com.zhongzhi.data.mapper.agent;


import com.zhongzhi.data.entity.agent.AgentRechargeTotalInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 代理商充值记录 Mapper 接口
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Repository
public interface AgentRechargeMapper {

    /**
     * 通过代理商Id查询代理商充值汇总信息
     *
     * @param agentIds 代理商I集
     * @return 理商充值汇总信息集合
     */
    List<AgentRechargeTotalInfo> selectAgentRechargeInfo(@Param("agentIds") List<Long> agentIds, @Param("category") Integer category);

    /**
     * 统计启用代理商的充值条数
     *
     * @return 启用代理商的充值条数
     */
    Long countEnableAgentRechargeAmount(@Param("category") Integer category);

    /**
     * 统计启用代理商的充值金额
     *
     * @return 启用代理商的充值金额
     */
    Long countEnableAgentRechargeMoney(@Param("category") Integer category);

    /**
     * 统计代理商充值总数
     *
     * @param agentId 代理商Id
     * @return 充值总数
     */
    Long countTotalNumForUpdate(@Param("agentId") Long agentId);

}
