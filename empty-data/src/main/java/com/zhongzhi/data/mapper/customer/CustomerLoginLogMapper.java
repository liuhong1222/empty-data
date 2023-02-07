package com.zhongzhi.data.mapper.customer;

import com.zhongzhi.data.entity.customer.CustomerLoginLog;
import com.zhongzhi.data.param.CustomerLoginLogQueryParam;
import com.zhongzhi.data.vo.customer.CustomerLoginLogQueryVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 客户登录日志 Mapper 接口
 * </pre>
 *
 * @author rivers
 * @since 2021-02-19
 */
@Repository
public interface CustomerLoginLogMapper {

    /**
     * 通过客户id计数
     * @date 2021/11/1
     * @param customerId
     * @return int
     */
    int countByCustomerId(Long customerId);

    /**
     * 客户登录日志-新增
     * @date 2021/11/1
     * @param customerLoginLog
     * @return int
     */
    int save(CustomerLoginLog customerLoginLog);

    /**
     * 客户登录日志-修改-通过客户id
     * @date 2021/11/1
     * @param customerLoginLog
     * @return int
     */
    int updateByCustomerId(CustomerLoginLog customerLoginLog);

    /**
     * 获取客户登录日志列表
     * @date 2021/11/1
     * @param customerLoginLogQueryParam
     * @return java.util.List<com.zhongzhi.data.vo.CustomerLoginLogQueryVo>
     */
    List<CustomerLoginLogQueryVo> page(CustomerLoginLogQueryParam customerLoginLogQueryParam);
}
