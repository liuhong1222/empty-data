package com.zhongzhi.data.service;

import com.zhongzhi.data.entity.Product;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.enums.ApplyStateEnum;
import com.zhongzhi.data.mapper.ProductMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品实现类
 * @author xybb
 * @date 2021-11-10
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductMapper productMapper;

    /**
     * 产品-列表
     * @date 2021/11/10
     * @param productGroupId 产品线id
     * @return List<Product>
     */
    public List<Product> listByCondition(String productGroupId) {
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        Product product = new Product();
        product.setAgentId(agentSettings.getAgentId());
        product.setApplyState(ApplyStateEnum.APPROVED.getCode());
        product.setState(1);

        if (StringUtils.isNotBlank(productGroupId)) {
            try {
                Long productGroupIdLong = Long.parseLong(productGroupId);
                product.setProductGroupId(productGroupIdLong);
            } catch (NumberFormatException e) {
                logger.error("数字类型转换错误", e);
            }
        }

        return productMapper.listByCondition(product);
    }

}
