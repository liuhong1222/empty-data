package com.zhongzhi.data.service;

import com.zhongzhi.data.entity.Faq;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.enums.ApplyStateEnum;
import com.zhongzhi.data.mapper.FaqMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品常见问题实现类
 * @author xybb
 * @date 2021-11-10
 */
@Service
public class FaqService {

    @Autowired
    private FaqMapper faqMapper;

    /**
     * 产品常见问题-列表
     * @date 2021/11/10
     * @param
     * @return List<Faq>
     */
    public List<Faq> list() {
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        Faq faq = new Faq();
        faq.setAgentId(agentSettings.getAgentId());
        faq.setApplyState(ApplyStateEnum.APPROVED.getCode());
        faq.setState(1);
        return faqMapper.listByCondition(faq);
    }
}
