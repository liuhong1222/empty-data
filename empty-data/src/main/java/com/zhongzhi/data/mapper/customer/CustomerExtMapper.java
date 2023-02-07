package com.zhongzhi.data.mapper.customer;



import com.zhongzhi.data.entity.customer.CustomerExt;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * 客户认证信息 Mapper 接口
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Repository
public interface CustomerExtMapper {

    /**
     * 客户认证信息-查找（通过客户id）
     * @date 2021/11/1
     * @param id
     * @return com.zhongzhi.data.entity.customer.CustomerExt
     */
    CustomerExt findByCustomerId(Long id);

    /**
     * 客户认证信息-新增
     * @date 2021/11/2
     * @param customerExt
     * @return int
     */
    int save(CustomerExt customerExt);

    /**
     * 客户认证信息-计数（通过社会信用代码）
     * @date 2021/11/3
     * @param socialCreditCode
     * @return int
     */
    int countByBusinessLicenseNumber(String socialCreditCode);

    /**
     * 客户认证信息-修改
     * @date 2021/11/24
     * @param customerExt
     * @return int
     */
    int update(CustomerExt customerExt);
}
