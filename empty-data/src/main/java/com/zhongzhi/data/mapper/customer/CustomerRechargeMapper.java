package com.zhongzhi.data.mapper.customer;

import com.zhongzhi.data.entity.customer.CustomerGiftTotalInfo;
import com.zhongzhi.data.entity.customer.CustomerRecharge;
import com.zhongzhi.data.entity.customer.CustomerRechargeTotalInfo;
import com.zhongzhi.data.param.FrontCustomerRechargeQueryParam;
import com.zhongzhi.data.vo.customer.CustomerRechargeQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 客户充值记录 Mapper 接口
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Repository
public interface CustomerRechargeMapper {

    /**
     * 通过id和充值类型来计数
     * @date 2021/10/31
     * @param id
     * @param code
     * @return int
     */
    int countByCustomerIdAndPayType(@Param("id") Long id, @Param("code") Integer code);

    /**
     * 客户充值记录-新增
     * @date 2021/10/31
     * @param recharge
     * @return int
     */
    int save(CustomerRecharge recharge);

    /**
     * 统计赠送信息
     *
     * @param customerIds 客户Id
     * @return 赠送信息
     */
    List<CustomerGiftTotalInfo> countGiftTotalNumber(@Param("customerIds") List<Long> customerIds, @Param("category") Integer category);

    /**
     * 查询指定客户Id集对应的充值信息
     *
     * @param customerIds 客户Id集
     * @return 客户充值信息
     */
    List<CustomerRechargeTotalInfo> countCustomerRechargeInfo(@Param("customerIds") List<Long> customerIds, @Param("category") Integer category);

    /**
     * 客户充值记录-查找（通过客户id和payType不等于）
     * @date 2021/11/3
     * @param customerId
     * @param payType
     * @return com.zhongzhi.data.entity.customer.CustomerRecharge
     */
    CustomerRecharge findByCustomerIdAndPayTypeNe(@Param("customerId") Long customerId, @Param("payType") int payType);

    List<CustomerRechargeQueryVo> getCustomerRechargePageListForFront(@Param("param") FrontCustomerRechargeQueryParam param);

    /**
     * 统计代理商为客户充值汇总信息
     *
     * @param agentIds
     * @return
     */
    List<CustomerRechargeQueryVo> countCustomerRechargeInfoByAgentIds(@Param("agentIds") List<Long> agentIds, @Param("category") Integer category);

    /**
     * 客户充值记录-查找（通过外部订单号）
     * @date 2021/11/8
     * @param outTradeNo
     * @return CustomerRecharge
     */
    CustomerRecharge selectByOrderNo(String outTradeNo);
}
