package com.zhongzhi.data.mapper.agent;

import com.zhongzhi.data.entity.agent.AgentSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author xybb
 * @date 2021-10-29
 */
@Mapper
public interface AgentSettingsMapper {

    /**
     * 代理商设置-查找（通过域名）
     * @date 2021/10/29
     * @param domain
     * @return com.zhongzhi.data.entity.agent.AgentSettings
     */
    AgentSettings findByDomainAudited(String domain);

    /**
     * 代理商设置-查找（通过代理商id和状态）
     * @date 2021/11/1
     * @param agentId
     * @return com.zhongzhi.data.entity.agent.AgentSettings
     */
    AgentSettings findByAgentIdAudited(Long agentId);

    /**
     * 代理商设置-查找（通过代理商id）
     * @date 2021/11/2
     * @param agentId
     * @return com.zhongzhi.data.entity.agent.AgentSettings
     */
    AgentSettings findByAgentId(Long agentId);

    /**
     * 代理商设置-通过官网类型查询代理商域名
     * @date 2021/11/30
     * @param officialWeb
     * @return String
     */
    String findAgentDomainByOfficialWeb(@Param("officialWeb") Integer officialWeb, @Param("phone") String phone);
}
