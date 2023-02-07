package com.zhongzhi.data.service.customer;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.customer.CustomerBalance;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.mapper.customer.CustomerBalanceMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 客户余额表
 * @author xybb
 * @date 2021-11-02
 */
@Service
public class CustomerBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerBalanceService.class);

    @Autowired
    private CustomerBalanceMapper customerBalanceMapper;

    /**
     * 客户余额记录-新增
     * @date 2021/11/2
     * @param customerBalance
     * @return com.zhongzhi.data.api.ApiResult
     */
    public ApiResult save(CustomerBalance customerBalance) {
        try {
            int i = customerBalanceMapper.save(customerBalance);
            if (i<=0) {
                logger.error("客户id：{}，新增客户余额记录失败。customerBalance:{}", customerBalance.getCustomerId(), customerBalance);
                return ApiResult.fail(ApiCode.DAO_EXCEPTION);
            } else {
                logger.info("客户id：{}，新增客户余额记录成功。customerBalance:{}", customerBalance.getCustomerId(), customerBalance);
                return ApiResult.ok();
            }
        } catch (Exception e) {
            logger.error("客户id：{}，新增客户余额记录异常，e:\n{}。customerBalance:{}", customerBalance.getCustomerId(), ExceptionUtils.getStackTrace(e), customerBalance);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION);
        }
    }

    /**
     * 客户余额记录-修改
     * @date 2021/11/8
     * @param customerBalance
     * @return void
     */
    public int update(CustomerBalance customerBalance) {
        int i = customerBalanceMapper.update(customerBalance);
        if (i<=0) {
            logger.error("客户id：{}，修改客户余额记录失败。customerBalance:{}", customerBalance.getCustomerId(), customerBalance);
        } else {
            logger.info("客户id：{}，修改客户余额记录成功。customerBalance:{}", customerBalance.getCustomerId(), customerBalance);
        }
        return i;
    }

    /**
     * 客户余额记录-查找（通过客户id）
     * @date 2021/11/9
     * @param customerId
     * @return CustomerBalance
     */
    public CustomerBalance findByCustomerId(Long customerId) {
        return customerBalanceMapper.findByCustomerId(customerId);
    }
}
