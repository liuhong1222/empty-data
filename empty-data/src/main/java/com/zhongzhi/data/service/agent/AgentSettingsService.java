package com.zhongzhi.data.service.agent;

import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.mapper.agent.AgentSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 代理商设置实现类
 * @author xybb
 * @date 2021-10-29
 */
@Service
public class AgentSettingsService {

    @Autowired
    private AgentSettingsMapper agentSettingsMapper;

    /**
     * 代理商设置-查找（通过域名和状态）
     * @date 2021/10/29
     * @param domain
     * @return com.zhongzhi.data.entity.agent.AgentSettings
     */
    public AgentSettings findByDomainAudited(String domain) {
        return agentSettingsMapper.findByDomainAudited(domain);
    }

    /**
     * 代理商设置-查找（通过代理商id和状态）
     * @date 2021/11/1
     * @param agentId
     * @return com.zhongzhi.data.entity.agent.AgentSettings
     */
    public AgentSettings findByAgentIdAudited(Long agentId) {
        return agentSettingsMapper.findByAgentIdAudited(agentId);
    }

    /**
     * 代理商设置-查找（通过代理商id）
     * @date 2021/11/1
     * @param agentId
     * @return com.zhongzhi.data.entity.agent.AgentSettings
     */
    public AgentSettings findByAgentId(Long agentId) {
        return agentSettingsMapper.findByAgentId(agentId);
    }

    /**
     * 代理商设置-通过官网类型查询代理商域名
     * @date 2021/11/30
     * @param officialWeb
     * @return String
     */
    public String findAgentDomainByOfficialWeb(Integer officialWeb, String phone) {
        return agentSettingsMapper.findAgentDomainByOfficialWeb(officialWeb, phone);
    }
}
