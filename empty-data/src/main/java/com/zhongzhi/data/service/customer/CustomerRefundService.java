package com.zhongzhi.data.service.customer;


import com.zhongzhi.data.entity.customer.CustomerRefundTotalInfo;
import com.zhongzhi.data.mapper.customer.CustomerRefundMapper;
import com.zhongzhi.data.service.agent.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * 客户退款记录 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Slf4j
@Service
public class CustomerRefundService {

    @Autowired
    private CustomerRefundMapper customerRefundMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AgentService agentService;

    @Transactional(isolation= Isolation.READ_UNCOMMITTED,rollbackFor=Exception.class)
    public List<CustomerRefundTotalInfo> countRefundInfo(List<Long> ids, Integer category) {
        return customerRefundMapper.countRefundInfo(ids, category);
    }

}
