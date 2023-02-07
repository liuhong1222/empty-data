package com.zhongzhi.data.service.customer;


import com.zhongzhi.data.entity.customer.CustomerConsume;
import com.zhongzhi.data.entity.customer.CustomerConsumeTotalInfo;
import com.zhongzhi.data.mapper.customer.CustomerConsumeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 客户消耗记录 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Slf4j
@Service
public class CustomerConsumeService {

    @Autowired
    private CustomerConsumeMapper customerConsumeMapper;

    @Transactional(isolation= Isolation.READ_UNCOMMITTED,rollbackFor=Exception.class)
    public List<CustomerConsumeTotalInfo> countConsumeInfo(List<Long> ids, List<CustomerConsume.ConsumeType> consumeTypes, Integer category) {
        List<Integer> types = new ArrayList<>(consumeTypes.size());
        for (CustomerConsume.ConsumeType t : consumeTypes) {
            types.add(t.getValue());
        }
        return customerConsumeMapper.countConsumeInfo(ids, types, category);
    }

    public int saveOne(CustomerConsume customerConsume) {
    	return customerConsumeMapper.saveOne(customerConsume);
    }
}
