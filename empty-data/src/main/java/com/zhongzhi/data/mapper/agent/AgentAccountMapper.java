package com.zhongzhi.data.mapper.agent;

import com.zhongzhi.data.entity.agent.AgentAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xybb
 * @date 2021-11-08
 */
@Mapper
public interface AgentAccountMapper {

    /**
     * 代理商账户-修改
     * @date 2021/11/8
     * @param agentAccount
     * @return int
     */
    int update(AgentAccount agentAccount);

    /**
     * 代理商账户-查找（通过代理商id）
     * @date 2021/11/9
     * @param agentId
     * @return AgentAccount
     */
    AgentAccount findByAgentId(Long agentId);
}
