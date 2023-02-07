package com.zhongzhi.data.mapper.customer;

import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.vo.customer.CustomerQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * @author xybb
 * @date 2021-10-28
 */
@Mapper
public interface CustomerMapper {

    /**
     * 客户-新增
     * @date 2021/10/28
     * @param customer
     * @return int
     */
    int save(Customer customer);

    /**
     * 客户-查询（通过id）
     * @date 2021/10/28
     * @param id
     * @return com.zhongzhi.data.entity.customer.Customer
     */
    Customer findById(Long id);

    /**
     * 统计ip地址数量
     * @date 2021/10/29
     * @param ip
     * @return int
     */
    int count(String ip);

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     */
    CustomerQueryVo getCustomerById(Serializable id);

    /**
     * 客户-查询（通过phone）
     * @date 2021/10/29
     * @param phone
     * @return com.zhongzhi.data.entity.customer.Customer
     */
    Customer findByPhone(@Param("phone") String phone, @Param("agentId") Long agentId);

    /**
     * 客户-修改
     * @date 2021/10/31
     * @param customer
     * @return int
     */
    int update(Customer customer);

    /**
     * 获取通过代理商id和时间条件过滤后的客户数量
     * @date 2021/10/31
     * @param agentId
     * @param date
     * @return int
     */
    int countByAgentIdAndCreateTime(@Param("agentId") Long agentId, @Param("date") String date);

    /**
     * 通过用户名、手机号、邮箱查找客户
     * @date 2021/11/2
     * @param account
     * @return com.zhongzhi.data.entity.customer.Customer
     */
    Customer selectByNameOrPhoneOrEmail(@Param("account") String account, @Param("agentId") Long agentId);

    /**
     * 客户-查询（通过邮箱）
     * @date 2021/11/4
     * @param email
     * @return com.zhongzhi.data.entity.customer.Customer
     */
    Customer findByEmail(@Param("email") String email, @Param("agentId") Long agentId);

    /**
     * 客户-修改-字段置null
     * @date 2021/11/23
     * @param customer
     * @return int
     */
    int setNull(Customer customer);
}
