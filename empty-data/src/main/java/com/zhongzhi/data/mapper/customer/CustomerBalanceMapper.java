package com.zhongzhi.data.mapper.customer;

import com.zhongzhi.data.entity.customer.CustomerBalance;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xybb
 * @date 2021-11-02
 */
@Mapper
public interface CustomerBalanceMapper {

    /**
     * 客户余额记录-新增
     * @date 2021/11/2
     * @param customerBalance
     * @return com.zhongzhi.data.api.ApiResult
     */
    int save(CustomerBalance customerBalance);

    /**
     * 客户余额记录-修改
     * @date 2021/11/8
     * @param customerBalance
     * @return int
     */
    int update(CustomerBalance customerBalance);

    /**
     * 客户余额记录-查找（通过客户id）
     * @date 2021/11/9
     * @param customerId
     * @return CustomerBalance
     */
    CustomerBalance findByCustomerId(Long customerId);
}
