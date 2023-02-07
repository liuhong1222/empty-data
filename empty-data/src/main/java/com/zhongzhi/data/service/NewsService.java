package com.zhongzhi.data.service;

import com.zhongzhi.data.entity.News;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.mapper.NewsMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 新闻实现类
 * @author xybb
 * @date 2021-11-11
 */
@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 新闻-列表（通过代理商id和审核状态）
     * @date 2021/11/11
     * @param
     * @return List<News>
     */
    public List<News> listByAgentIdAudited(String domain) {
        return newsMapper.listByAgentIdAudited(domain);
    }

}
