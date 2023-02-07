package com.zhongzhi.data.service;

import com.zhongzhi.data.entity.ProductGroup;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.enums.ApplyStateEnum;
import com.zhongzhi.data.mapper.ProductGroupMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品线实现类
 * @author xybb
 * @date 2021-11-11
 */
@Service
public class ProductGroupService {

    @Autowired
    private ProductGroupMapper productGroupMapper;

    /**
     * 产品线管理分页列表
     * @date 2021/11/11
     * @param
     * @return List<ProductGroup>
     */
    public List<ProductGroup> listByCondition() {
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        ProductGroup productGroupTemp = new ProductGroup();
        productGroupTemp.setAgentId(agentSettings.getAgentId());
        productGroupTemp.setApplyState(ApplyStateEnum.APPROVED.getCode());
        productGroupTemp.setState(1);
        return productGroupMapper.listByCondition(productGroupTemp);
    }
}
