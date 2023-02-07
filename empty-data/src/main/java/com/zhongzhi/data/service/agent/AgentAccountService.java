package com.zhongzhi.data.service.agent;

import com.zhongzhi.data.entity.agent.AgentAccount;
import com.zhongzhi.data.mapper.agent.AgentAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 代理商账户实现类
 * @author xybb
 * @date 2021-11-08
 */
@Service
public class AgentAccountService {

    private static final Logger logger = LoggerFactory.getLogger(AgentAccountService.class);

    @Autowired
    private AgentAccountMapper agentAccountMapper;

    /**
     * 代理商账户-修改
     * @date 2021/11/8
     * @param agentAccount
     * @return void
     */
    public int update(AgentAccount agentAccount) {
        int i = agentAccountMapper.update(agentAccount);
        if (i<=0) {
            logger.error("代理商id：{}，修改代理商账户记录失败。agentAccount:{}", agentAccount.getAgentId(), agentAccount);
        } else {
            logger.info("代理商id：{}，修改代理商账户记录成功。agentAccount:{}", agentAccount.getAgentId(), agentAccount);
        }
        return i;
    }

    /**
     * 代理商账户-查找（通过代理商id）
     * @date 2021/11/9
     * @param agentId
     * @return AgentAccount
     */
    public AgentAccount findByAgentId(Long agentId) {
        return agentAccountMapper.findByAgentId(agentId);
    }
}
