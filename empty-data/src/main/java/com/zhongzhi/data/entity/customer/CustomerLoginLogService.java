package com.zhongzhi.data.entity.customer;


import com.zhongzhi.data.mapper.customer.CustomerLoginLogMapper;
import com.zhongzhi.data.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <pre>
 * 客户登录日志 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2021-02-19
 */
@Service
public class CustomerLoginLogService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerLoginLogService.class);

    @Autowired
    private CustomerLoginLogMapper customerLoginLogMapper;

    @Autowired
    private Snowflake snowflake;

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public boolean saveCustomerLoginLog(CustomerLoginLog customerLoginLog) throws Exception {
        return this.saveOrUpdate(customerLoginLog);
    }

    /**
     * 无则增，有则改
     * @date 2021/11/1
     * @param customerLoginLog
     * @return boolean
     */
    public boolean saveOrUpdate(CustomerLoginLog customerLoginLog) {
        int i = customerLoginLogMapper.countByCustomerId(customerLoginLog.getCustomerId());
        if (i<1) {
            // 新增
            customerLoginLog.setId(snowflake.nextId());
            int j = customerLoginLogMapper.save(customerLoginLog);
            if (j<=0) {
                logger.error("客户id：{}，新增客户登录日志记录失败。customerLoginLog:{}", customerLoginLog.getCustomerId(), customerLoginLog);
                return false;
            } else {
                logger.info("客户id：{}，新增客户登录日志记录成功。customerLoginLog:{}", customerLoginLog.getCustomerId(), customerLoginLog);
                return true;
            }

        } else {
            // 修改。如果有多条，修改最新那条。
            int k = customerLoginLogMapper.updateByCustomerId(customerLoginLog);
            if (k<=0) {
                logger.error("客户id：{}，修改客户登录日志记录失败。customerLoginLog:{}", customerLoginLog.getCustomerId(), customerLoginLog);
                return false;
            } else {
                logger.info("客户id：{}，修改客户登录日志记录成功。customerLoginLog:{}", customerLoginLog.getCustomerId(), customerLoginLog);
                return true;
            }
        }
    }

    // public PageInfo<CustomerLoginLogQueryVo> getCustomerLoginLogPageList(CustomerLoginLogQueryParam customerLoginLogQueryParam) throws Exception {
    //     PageHelper.startPage(customerLoginLogQueryParam.getPage(), customerLoginLogQueryParam.getSize());
    //     List<CustomerLoginLogQueryVo> list = customerLoginLogMapper.page(customerLoginLogQueryParam);
    //     PageInfo<CustomerLoginLogQueryVo> info = new PageInfo<>(list);
    //     return info;
    // }

}
