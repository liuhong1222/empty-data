package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xybb
 * @date 2021-11-11
 */
@Mapper
public interface NewsMapper {

    /**
     * 新闻-列表（通过代理商id和审核状态）
     * @date 2021/11/11
     * @param agentId
     * @return List<News>
     */
    List<News> listByAgentIdAudited(@Param("domain")String domain);
}
