package com.zhongzhi.data.service.customer;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.customer.*;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.enums.RechargeStateEnum;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.mapper.customer.CustomerOrderRecordMapper;
import com.zhongzhi.data.mapper.customer.CustomerRechargeMapper;
import com.zhongzhi.data.param.FrontCustomerRechargeQueryParam;
import com.zhongzhi.data.util.Snowflake;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.customer.CustomerQueryVo;
import com.zhongzhi.data.vo.customer.CustomerRechargeQueryVo;
import com.zhongzhi.data.vo.login.LoginSysUserRedisVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 客户充值记录 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Service
public class CustomerRechargeService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRechargeService.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRechargeMapper customerRechargeMapper;

    @Autowired
    private CustomerBalanceService customerBalanceService;

    @Autowired
    private CustomerOrderRecordMapper customerOrderRecordMapper;

    @Autowired
    private Snowflake snowflake;

    public ApiResult<Boolean> saveRegisterGift(Customer customer, LoginSysUserRedisVo loginSysUserRedisVo, CustomerBalance customerBalance) throws Exception {
        int count = customerRechargeMapper.countByCustomerIdAndPayType(customer.getId(), RechargeStateEnum.REGISTER.getCode());

        if (count > 0) {
            return ApiResult.fail("已赠送");
        }
        CustomerQueryVo customerQueryVo = customerService.getCustomerWithEmptyAccountInfo(customer.getId());
        customerService.addRealtimeAccountInfo(Collections.singletonList(customerQueryVo),
                Arrays.asList(CustomerConsume.ConsumeType.DEDUCTION_SUCCESS, CustomerConsume.ConsumeType.FREEZE));
        // 赠送
        CustomerRecharge recharge = new CustomerRecharge();
        recharge.setId(snowflake.nextId());
        recharge.setRechargeNumber(5000);
        recharge.setPrice("0");
        recharge.setPhone(customer.getPhone());
        // 充值类型，0：对公转账，1：支付宝扫码付，2：注册赠送，3：赠送, 4：对公支付宝转账，5：对私支付宝， 6：对私微信，7：对私转账
        recharge.setPayType(RechargeStateEnum.REGISTER.getCode());
        recharge.setPaymentAmount("0");
        recharge.setOrderNo(customer.getId() + "gift");
        recharge.setName(customer.getName());
        recharge.setCustomerId(customer.getId());
        recharge.setCreateTime(new Date());
        recharge.setAgentId(customer.getAgentId());
        recharge.setGoodsName("注册赠送");
        recharge.setRemark("注册赠送");
        recharge.setVersion(0);
        recharge.setCategory(0);
        recharge.setCreatorName(loginSysUserRedisVo == null ? "system" : loginSysUserRedisVo.getUsername());
        recharge.setOpeningBalance(customerQueryVo.getRemainNumberTotal());
        recharge.setClosingBalance(new BigDecimal(customerQueryVo.getRemainNumberTotal() + "").add(new BigDecimal(recharge.getRechargeNumber() + "")).longValue());

        int i = customerRechargeMapper.save(recharge);
        if (i<=0) {
            logger.error("客户id：{}，新增客户充值记录失败。recharge:{}", recharge.getCustomerId(), recharge);
            return ApiResult.fail(ApiCode.DAO_EXCEPTION, false);
        } else {
            logger.info("客户id：{}，新增客户充值记录成功。recharge:{}", recharge.getCustomerId(), recharge);

            // 更新mysql余额
            customerBalance.setEmptyCount(5000L);
            customerBalance.setEmptyRechargeNum(5000L);
            int j = customerBalanceService.update(customerBalance);
            if (j<=0) {
                logger.error("注册赠送-修改客户余额记录失败。客户id：{}，。customerBalance:{}", customerBalance.getCustomerId(), customerBalance);
                throw new BusinessException("数据库处理失败");
            }
            logger.info("注册赠送-修改客户余额记录表成功。客户id：{}，customerBalance:{}", customerBalance.getCustomerId(), customerBalance);
            return ApiResult.ok(true);
        }
    }

    @Transactional(isolation= Isolation.DEFAULT,rollbackFor=Exception.class)
    public List<CustomerGiftTotalInfo> countGiftTotalNumber(List<Long> ids, Integer category) {
        return customerRechargeMapper.countGiftTotalNumber(ids, category);
    }

    @Transactional(isolation= Isolation.DEFAULT,rollbackFor=Exception.class)
    public List<CustomerRechargeTotalInfo> countCustomerRechargeInfo(List<Long> ids, Integer category) {
        return customerRechargeMapper.countCustomerRechargeInfo(ids, category);
    }

    /**
     * 客户充值记录-查找（通过客户id和payType不等于）
     * @date 2021/11/3
     * @param customerId
     * @param payType payType不等于
     * @return com.zhongzhi.data.entity.customer.CustomerRecharge
     */
    public CustomerRecharge findByCustomerIdAndPayTypeNe(Long customerId, int payType) {
        return customerRechargeMapper.findByCustomerIdAndPayTypeNe(customerId, payType);
    }

    public PageInfo<CustomerRechargeQueryVo> getCustomerRechargePageListForFront(FrontCustomerRechargeQueryParam param) {
        // 设置参数
        Customer customer = ThreadLocalContainer.getCustomer();
        param.setCustomerId(customer.getId());
        if (param.getCreateTimeEnd() != null) {
            param.setCreateTimeEnd(DateUtil.endOfDay(param.getCreateTimeEnd()));
        }

        // 查询数据
        PageHelper.startPage(param.getPage(), param.getSize());
        List<CustomerRechargeQueryVo> list = customerRechargeMapper.getCustomerRechargePageListForFront(param);
        PageInfo<CustomerRechargeQueryVo> info = new PageInfo<>(list);
        return info;
    }

    public boolean saveByAlipayOutTradeNo(String outTradeNo) throws Exception {
        CustomerOrderRecord customerOrderRecord = customerOrderRecordMapper.findByOutOrderId(outTradeNo);
        if (customerOrderRecord == null) {
            logger.error("未能找到客户订单记录{}", outTradeNo);
            return false;
        }

        CustomerQueryVo customerQueryVo = customerService.getCustomerWithEmptyAccountInfo(customerOrderRecord.getCustomerId());
        CustomerRecharge customerRecharge = selectByOrderNo(outTradeNo);
        if (customerRecharge != null) {
            logger.info("客户充值记录已存在，不需要再次保存{}", outTradeNo);
            return false;
        }

        customerRecharge = new CustomerRecharge();
        customerRecharge.setAgentId(customerOrderRecord.getAgentId())
                .setCreateTime(new Date())
                .setUpdateTime(customerRecharge.getCreateTime()).setCustomerId(customerOrderRecord.getCustomerId())
                .setGoodsName(customerOrderRecord.getGoodsName()).setOrderNo(outTradeNo)
                .setPaymentAmount(customerOrderRecord.getGoodsPrice())
                // 充值类型，0：对公转账，1：支付宝扫码付，2：注册赠送，3：赠送, 4：对公支付宝转账，5：对私支付宝， 6：对私微信，7：对私转账 ，8：微信扫码付
                .setPayType(customerOrderRecord.getPayType()).setPrice(
                new BigDecimal(customerOrderRecord.getGoodsPrice())
                        .divide(new BigDecimal(customerOrderRecord.getGoodsSpecifications()), 5,
                                BigDecimal.ROUND_CEILING).toString())
                .setRechargeNumber(new BigDecimal(customerOrderRecord.getGoodsSpecifications()).intValue())
                .setPhone(customerQueryVo.getPhone()).setVersion(0).setRemark("扫码付自助充值")
                .setName(customerQueryVo.getName()).setCreatorName("system")
                .setCategory(customerOrderRecord.getCategory());
        if(ProductTypeEnum.REALTIME.getCode().equals(customerOrderRecord.getCategory())) {
            customerRecharge.setOpeningBalance(customerQueryVo.getRealtimeBalance());
            customerRecharge.setClosingBalance(new BigDecimal(customerQueryVo.getRealtimeBalance() + "")
                    .add(new BigDecimal(customerRecharge.getRechargeNumber() + "")).longValue());
        } else if(ProductTypeEnum.EMPTY.getCode().equals(customerOrderRecord.getCategory())) {
            customerRecharge.setOpeningBalance(customerQueryVo.getRemainNumberTotal());
            customerRecharge.setClosingBalance(new BigDecimal(customerQueryVo.getRemainNumberTotal() + "")
                    .add(new BigDecimal(customerRecharge.getRechargeNumber() + "")).longValue());
        }else {
        	customerRecharge.setOpeningBalance(customerQueryVo.getInternationalBalance());
            customerRecharge.setClosingBalance(new BigDecimal(customerQueryVo.getInternationalBalance() + "")
                    .add(new BigDecimal(customerRecharge.getRechargeNumber() + "")).longValue());
        }

        customerRecharge.setId(snowflake.nextId());
        return customerRechargeMapper.save(customerRecharge) > 0;
    }

    /**
     * 客户充值记录-查找（通过外部订单号）
     * @date 2021/11/8
     * @param outTradeNo
     * @return CustomerRecharge
     */
    @Transactional(readOnly = true)
    public CustomerRecharge selectByOrderNo(String outTradeNo) {
        return customerRechargeMapper.selectByOrderNo(outTradeNo);
    }
}
