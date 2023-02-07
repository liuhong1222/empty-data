package com.zhongzhi.data.mapper.agent;

import com.zhongzhi.data.entity.agent.Agent;
import com.zhongzhi.data.vo.AgentQueryVo;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;

/**
 * @author xybb
 * @date 2021-10-29
 */
@Mapper
public interface AgentMapper {

    /**
     * 代理商-查找（通过id）
     * @date 2021/10/29
     * @param id
     * @return com.zhongzhi.data.entity.agent.Agent
     */
    Agent findById(Long id);

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     */
    AgentQueryVo getAgentById(Serializable id);
}
