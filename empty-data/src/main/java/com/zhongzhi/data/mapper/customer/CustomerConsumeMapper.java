package com.zhongzhi.data.mapper.customer;


import com.zhongzhi.data.entity.customer.CustomerConsume;
import com.zhongzhi.data.entity.customer.CustomerConsumeTotalInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * 客户消耗记录 Mapper 接口
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Repository
public interface CustomerConsumeMapper {

    /**
     * 统计客户消费数量
     *
     * @param customerIds 客户Id
     * @param types       客户消费类型
     * @return 客户消费数量
     */
    List<CustomerConsumeTotalInfo> countConsumeInfo(@Param("customerIds") List<Long> customerIds,
                                                    @Param("types") List<Integer> types,
                                                    @Param("category") Integer category);
    
    int saveOne(CustomerConsume customerConsume);
}
