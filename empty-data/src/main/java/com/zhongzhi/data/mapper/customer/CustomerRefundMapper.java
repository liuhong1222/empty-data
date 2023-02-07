package com.zhongzhi.data.mapper.customer;


import com.zhongzhi.data.entity.customer.CustomerRefundTotalInfo;
import com.zhongzhi.data.vo.customer.CustomerRefundQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 客户退款记录 Mapper 接口
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Repository
public interface CustomerRefundMapper {

    /**
     * 统计客户退款信息
     *
     * @param customerIds 客户Id集
     * @return 客户退款信息
     */
    List<CustomerRefundTotalInfo> countRefundInfo(@Param("customerIds") List<Long> customerIds, @Param("category") Integer category);

    /**
     * 统计代理商下所有客户退款条数
     * @date 2021/11/8
     * @param agentIds
     * @param category
     * @return List<CustomerRefundQueryVo>
     */
    List<CustomerRefundQueryVo> countCustomerRefundInfoByAgentIds(@Param("agentIds") List<Long> agentIds, @Param("category") Integer category);
}
