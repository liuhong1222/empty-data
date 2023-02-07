package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xybb
 * @date 2021-10-29
 */
@Mapper
public interface SysUserMapper {

    List<SysUser> selectByAgentId(Long agentId);
}
