package com.zhongzhi.data.service.customer;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.RedisConstant;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.*;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.mapper.customer.CustomerMapper;
import com.zhongzhi.data.param.*;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.service.front.LoginService;
import com.zhongzhi.data.service.sys.RefreshCacheService;
import com.zhongzhi.data.util.*;
import com.zhongzhi.data.vo.customer.CustomerQueryVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客户实现类
 * @author xybb
 * @date 2021-10-28
 */
@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private CustomerExtService customerExtService;

    @Autowired
    private CustomerRechargeService customerRechargeService;

    @Autowired
    private CustomerConsumeService customerConsumeService;

    @Autowired
    private CustomerRefundService customerRefundService;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CustomerBalanceService customerBalanceService;

    @Autowired
    private RefreshCacheService refreshCacheService;

    /**
     * 客户-新增
     * @date 2021/10/28
     * @param customer
     * @return com.zhongzhi.data.common.api.ApiResult
     */
    public ApiResult save(Customer customer) {
        try {
            int i = customerMapper.save(customer);
            if (i<=0) {
                logger.error("客户id：{}，新增客户失败。customer:{}", customer.getId(), customer);
                return ApiResult.fail(ApiCode.DAO_EXCEPTION);
            } else {
                logger.info("客户id：{}，新增客户成功。customer:{}", customer.getId(), customer);
                return ApiResult.ok();
            }
        } catch (Exception e) {
            logger.info("客户id：{}，新增客户出现异常：e:{}。customer:{}", ThreadLocalContainer.getCustomerId(), ExceptionUtils.getStackTrace(e), customer);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION);
        }
    }

    /**
     * 客户-查询（通过id）
     * @date 2021/10/28
     * @param id
     * @return com.zhongzhi.data.common.api.ApiResult
     */
    public Customer findById(Long id) {
        return customerMapper.findById(id);
    }

    /**
     * 客户-查询（通过phone）
     * @date 2021/10/28
     * @param phone
     * @return com.zhongzhi.data.common.api.ApiResult
     */
    public Customer findByPhone(String phone, Long agentId) {
        return customerMapper.findByPhone(phone, agentId);
    }


    /**
     * 统计ip地址数量
     * @date 2021/10/29
     * @param ip
     * @return int
     */
    public int count(String ip) {
        return customerMapper.count(ip);
    }

    /**
     * 客户-修改
     * @date 2021/10/29
     * @param customer
     * @return int
     */
    public int update(Customer customer) {
        int i = 0;
        try {
            i = customerMapper.update(customer);
            if (i<=0) {
                logger.error("客户id：{}，修改客户信息失败。customer:{}", customer.getId(), customer);
            } else {
                // 刷新缓存
                refreshCacheService.customerInfoRefresh(customer.getId());
                redisClient.remove(RedisConstant.CUSTOMER_VO_KEY+customer.getId());
                redisClient.remove(RedisConstant.CUSTOMER_EXT_KEY+customer.getId());

                // 更新登录信息缓存，解压密码脱敏
                Customer customerCache = this.findById(customer.getId());
                String unzipPwd = setUnknown(customerCache);
                customerCache.setUnzipPassword(unzipPwd);
                redisClient.set(Constant.FRONT_LOGIN_USER+customerCache.getAgentId()+"-"+customerCache.getPhone(), JSONObject.toJSONString(customerCache), Constant.ONE_DAY);

                logger.info("客户id：{}，修改客户信息成功。customer:{}", customer.getId(), customer);
            }
        } catch (Exception e) {
            logger.error("客户id：{}，修改客户信息出现异常。e:\n{}, customer:{}", ThreadLocalContainer.getCustomerId(), ExceptionUtils.getStackTrace(e), customer);
        }

        return i;
    }

    /**
     * 客户-修改-字段置null
     * @date 2021/10/29
     * @param customer
     * @return int
     */
    public int setNull(Customer customer) {
        int i = 0;
        try {
            i = customerMapper.setNull(customer);
            if (i<=0) {
                logger.error("客户id：{}，修改客户信息失败。customer:{}", customer.getId(), customer);
            } else {
                // 刷新缓存
                refreshCacheService.customerInfoRefresh(customer.getId());
                redisClient.remove(RedisConstant.CUSTOMER_VO_KEY+customer.getId());
                redisClient.remove(RedisConstant.CUSTOMER_EXT_KEY+customer.getId());

                // 更新登录信息缓存，解压密码脱敏
                Customer customerCache = this.findById(customer.getId());
                String unzipPwd = setUnknown(customerCache);
                customerCache.setUnzipPassword(unzipPwd);
                redisClient.set(Constant.FRONT_LOGIN_USER+customerCache.getAgentId()+"-"+customerCache.getPhone(), JSONObject.toJSONString(customerCache), Constant.ONE_DAY);

                logger.info("客户id：{}，修改客户信息成功。customer:{}", customer.getId(), customer);
            }
        } catch (Exception e) {
            logger.error("客户id：{}，修改客户信息出现异常。e:\n{}, customer:{}", ThreadLocalContainer.getCustomerId(), ExceptionUtils.getStackTrace(e), customer);
        }

        return i;
    }

    /**
     * 解压密码脱敏处理
     * @date 2021/11/20
     * @param customer
     * @return String
     */
    private String setUnknown(Customer customer) {
        String unzipPassword = customer.getUnzipPassword();
        if (!StringUtils.isBlank(unzipPassword)) {
            char first = unzipPassword.charAt(0);
            char last = unzipPassword.charAt(unzipPassword.length()-1);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < unzipPassword.length()-2; i++) {
                sb.append("*");
            }
            return first+sb.toString()+last;
        } else {
            return null;
        }
    }

    /**
     * 解压密码脱敏处理
     * @date 2021/11/20
     * @param customer
     * @return String
     */
    private String setUnknown2(CustomerQueryVo customer) {
        String unzipPassword = customer.getUnzipPassword();
        if (!StringUtils.isBlank(unzipPassword)) {
            char first = unzipPassword.charAt(0);
            char last = unzipPassword.charAt(unzipPassword.length()-1);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < unzipPassword.length()-2; i++) {
                sb.append("*");
            }
            return first+sb.toString()+last;
        } else {
            return null;
        }
    }

    /**
     * 获取redis缓存信息
     * @date 2021/11/4
     * @param id
     * @return com.zhongzhi.data.vo.CustomerQueryVo
     */
    @Transactional
    public CustomerQueryVo getCustomerWithEmptyAccountInfo(Long id) throws Exception {
        String voKey = RedisConstant.CUSTOMER_VO_KEY+id;
        String voString = redisClient.get(voKey);
        CustomerQueryVo customerQueryVo = JSONObject.parseObject(voString, CustomerQueryVo.class);
        if(customerQueryVo == null) {
            // 1.客户信息
            customerQueryVo = customerMapper.getCustomerById(id);
            // 解压密码脱敏
            String unzipPwd = setUnknown2(customerQueryVo);
            customerQueryVo.setUnzipPassword(unzipPwd);

            // 2.客户认证信息
            String extKey = RedisConstant.CUSTOMER_EXT_KEY+id;
            String extString = redisClient.get(extKey);
            CustomerExt ext = JSONObject.parseObject(extString, CustomerExt.class);
            if(ext == null) {
                ext = new CustomerExt();
                ext.setCustomerId(id);
                ext = customerExtService.findByCustomerId(id);

                redisClient.set(extKey, JSONObject.toJSONString(ext), 600);
            }
            customerQueryVo.setCustomerExt(ext);
            redisClient.set(voKey, JSONObject.toJSONString(customerQueryVo), 600);
        }
        // 3.查询余额。从redis查余额，没有再查数据库。每次调用都重新查询余额
        getCustomerBalance(id, customerQueryVo);

        return customerQueryVo;
    }

    /**
     * 获取客户余额信息，先查redis，没有再查数据库。
     * @date 2021/11/10
     * @param id
     * @param customerQueryVo
     * @return void
     */
    private void getCustomerBalance(Long id, CustomerQueryVo customerQueryVo) {
    	CustomerBalance customerBalance = null;
    	// 空号检测余额
        String emptyStr = redisClient.get(RedisConstant.EMPTY_BALANCE_KEY + id);
        // 实时检测余额
        String realtimeStr = redisClient.get(RedisConstant.REALTIME_BALANCE_KEY + id);
        // 国际检测余额
        String internationalStr = redisClient.get(RedisConstant.INTERNATIONAL_BALANCE_KEY + id);
        // 国际检测余额
        String directCommonStr = redisClient.get(RedisConstant.DIRECT_COMMON_BALANCE_KEY + id);
        // 国际检测余额
        String lineDirectStr = redisClient.get(RedisConstant.LINE_DIRECT_BALANCE_KEY + id);
        if (StringUtils.isBlank(emptyStr) || StringUtils.isBlank(realtimeStr) || StringUtils.isBlank(internationalStr)
        		 || StringUtils.isBlank(directCommonStr)  || StringUtils.isBlank(lineDirectStr)) {
            // 查数据库
            customerBalance = customerBalanceService.findByCustomerId(id);
        }

        customerQueryVo.setRemainNumberTotal(StringUtils.isNotBlank(emptyStr)?Long.valueOf(emptyStr):customerBalance.getEmptyCount());
        customerQueryVo.setRealtimeBalance(StringUtils.isNotBlank(realtimeStr)?Long.valueOf(realtimeStr):customerBalance.getRealtimeCount());
        customerQueryVo.setInternationalBalance(StringUtils.isNotBlank(internationalStr)?Long.valueOf(internationalStr):customerBalance.getInternationalCount());
        customerQueryVo.setDirectCommonBalance(StringUtils.isNotBlank(directCommonStr)?Long.valueOf(directCommonStr):customerBalance.getDirectCommonCount());
        customerQueryVo.setLineDirectBalance(StringUtils.isNotBlank(lineDirectStr)?Long.valueOf(lineDirectStr):customerBalance.getLineDirectCount());
    }

    /**
     * 获取空号检测账户充值、消费、退款、赠送等信息
     * 客户余额 = 赠送条数+充值条数-消耗条数-退款条数
     *
     * @param customers 客户列表
     */
    @Transactional
    public void addEmptyAccountInfo(List<CustomerQueryVo> customers, List<CustomerConsume.ConsumeType> consumeTypes) {
        // 统计字段初始化
        emptyAccountInit(customers);

        List<Long> ids = customers.stream().map(CustomerQueryVo::getId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 赠送条数
        List<CustomerGiftTotalInfo> giftInfos = customerRechargeService.countGiftTotalNumber(ids, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtils.isNotEmpty(giftInfos)) {
            customers.forEach(c -> {
                Optional<CustomerGiftTotalInfo> giftInfo = giftInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                giftInfo.ifPresent(r -> {
                    c.setGiftNumber(r.getTotalNumber());
                    // 客户剩余条数
                    c.setRemainNumberTotal(c.getRemainNumberTotal() + r.getTotalNumber());
                });
            });
        }

        // 充值信息
        List<CustomerRechargeTotalInfo> rechargeInfos = customerRechargeService.countCustomerRechargeInfo(ids, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtils.isNotEmpty(rechargeInfos)) {
            customers.forEach(c -> {
                Optional<CustomerRechargeTotalInfo> rechargeInfo = rechargeInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                rechargeInfo.ifPresent(r -> {
                    c.setRechargeNumberTotal(r.getRechargeNumberTotal());
                    c.setPaymentAmountTotal(r.getPaymentAmountTotal());
                    c.setRemainNumberTotal(c.getRemainNumberTotal() + r.getRechargeNumberTotal());
                });
                if (!rechargeInfo.isPresent()) {
                    c.setRechargeNumberTotal(0);
                    c.setPaymentAmountTotal("0");
                }
            });
        }

        // 消费信息
        List<CustomerConsumeTotalInfo> consumeInfos = customerConsumeService.countConsumeInfo(ids, consumeTypes, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtils.isNotEmpty(consumeInfos)) {
            customers.forEach(c -> {
                Optional<CustomerConsumeTotalInfo> consumeInfo = consumeInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                consumeInfo.ifPresent(r -> {
                    c.setEmptyConsumeTotalCount(r.getConsumeNumberTotal());
                    c.setRemainNumberTotal(c.getRemainNumberTotal() - r.getConsumeNumberTotal());
                    if (c.getRemainNumberTotal() < 0) {
                        c.setRemainNumberTotal(0L);
                    }
                });
            });
        }

        // 退款信息
        List<CustomerRefundTotalInfo> refundTotalInfos = customerRefundService.countRefundInfo(ids, ProductTypeEnum.EMPTY.getCode());
        if (CollectionUtils.isNotEmpty(refundTotalInfos)) {
            customers.forEach(c -> {
                Optional<CustomerRefundTotalInfo> refundInfo = refundTotalInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                refundInfo.ifPresent(r -> {
                    c.setEmptyRefundTotalPay(r.getRefundTotalPay());
                    c.setEmptyConsumeTotalCount(r.getRefundNumberTotal());
                    c.setRemainNumberTotal(c.getRemainNumberTotal() - r.getRefundNumberTotal());
                    if (c.getRemainNumberTotal() < 0) {
                        c.setRemainNumberTotal(0L);
                    }
                });
            });
        }
    }

    /**
     * 获取实时检测账户充值、消费、退款、赠送等信息
     * 客户余额 = 赠送条数+充值条数-消耗条数-退款条数
     *
     * @param customers 客户列表
     */
    @Transactional
    public void addRealtimeAccountInfo(List<CustomerQueryVo> customers, List<CustomerConsume.ConsumeType> consumeTypes) {
        // 统计字段初始化
        realtimeAccountInit(customers);

        List<Long> ids = customers.stream().map(CustomerQueryVo::getId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 赠送条数
        List<CustomerGiftTotalInfo> giftInfos = customerRechargeService.countGiftTotalNumber(ids, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtils.isNotEmpty(giftInfos)) {
            customers.forEach(c -> {
                Optional<CustomerGiftTotalInfo> giftInfo = giftInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                giftInfo.ifPresent(r -> {
                    c.setRealtimeGiftTotalCount(r.getTotalNumber());
                    // 客户剩余条数
                    c.setRealtimeBalance(c.getRealtimeBalance() + r.getTotalNumber());
                });
            });
        }

        // 充值信息
        List<CustomerRechargeTotalInfo> rechargeInfos = customerRechargeService.countCustomerRechargeInfo(ids, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtils.isNotEmpty(rechargeInfos)) {
            customers.forEach(c -> {
                Optional<CustomerRechargeTotalInfo> rechargeInfo = rechargeInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                rechargeInfo.ifPresent(r -> {
                    c.setRealtimeRechargeTotalCount(r.getRechargeNumberTotal());
                    c.setRealtimeRechargeTotalPay(r.getPaymentAmountTotal());
                    c.setRealtimeBalance(c.getRealtimeBalance() + r.getRechargeNumberTotal());
                });
                if (!rechargeInfo.isPresent()) {
                    c.setRealtimeRechargeTotalCount(0);
                    c.setRealtimeRechargeTotalPay("0");
                }
            });
        }

        // 消费信息
        List<CustomerConsumeTotalInfo> consumeInfos = customerConsumeService.countConsumeInfo(ids, consumeTypes, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtils.isNotEmpty(consumeInfos)) {
            customers.forEach(c -> {
                Optional<CustomerConsumeTotalInfo> consumeInfo = consumeInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                consumeInfo.ifPresent(r -> {
                    c.setRealtimeConsumeTotalCount(r.getConsumeNumberTotal());
                    c.setRealtimeBalance(c.getRealtimeBalance() - r.getConsumeNumberTotal());
                    if (c.getRealtimeBalance() < 0) {
                        c.setRealtimeBalance(0L);
                    }
                });
            });
        }

        // 退款信息
        List<CustomerRefundTotalInfo> refundTotalInfos = customerRefundService.countRefundInfo(ids, ProductTypeEnum.REALTIME.getCode());
        if (CollectionUtils.isNotEmpty(refundTotalInfos)) {
            customers.forEach(c -> {
                Optional<CustomerRefundTotalInfo> refundInfo = refundTotalInfos.stream()
                        .filter(r -> c.getId().equals(r.getCustomerId())).findAny();

                refundInfo.ifPresent(r -> {
                    c.setRealtimeRefundTotalPay(r.getRefundTotalPay());
                    c.setRealtimeRefundTotalCount(r.getRefundNumberTotal());
                    c.setRealtimeBalance(c.getRealtimeBalance() - r.getRefundNumberTotal());
                    if (c.getRealtimeBalance() < 0) {
                        c.setRealtimeBalance(0L);
                    }
                });
            });
        }
    }

    /**
     * 获取通过代理商id和时间条件过滤后的客户数量
     * @date 2021/10/31
     * @param agentId
     * @param date
     * @return int
     */
    public int countByAgentIdAndCreateTime(Long agentId, String date) {
        return customerMapper.countByAgentIdAndCreateTime(agentId, date);
    }

    private void emptyAccountInit(List<CustomerQueryVo> customers) {
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }
        customers.forEach(customer -> {
            customer.setRemainNumberTotal(0)
                    .setRechargeNumberTotal(0)
                    .setEmptyConsumeTotalCount(0)
                    .setEmptyRefundTotalCount(0)
                    .setGiftNumber(0)
                    .setEmptyRefundTotalPay("0")
                    .setPaymentAmountTotal("0")
            ;
        });
    }

    private void realtimeAccountInit(List<CustomerQueryVo> customers) {
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }
        customers.forEach(customer -> {
            customer.setRealtimeBalance(0)
                    .setRealtimeRechargeTotalCount(0)
                    .setRealtimeConsumeTotalCount(0)
                    .setRealtimeRefundTotalCount(0)
                    .setRealtimeGiftTotalCount(0)
                    .setRealtimeRefundTotalPay("0")
                    .setRealtimeRechargeTotalPay("0")
            ;
        });
    }

    /**
     * 通过用户名、手机号、邮箱查找客户
     * @date 2021/11/2
     * @param account
     * @return com.zhongzhi.data.entity.customer.Customer
     */
    public Customer selectByNameOrPhoneOrEmail(String account, Long agentId) {
        return customerMapper.selectByNameOrPhoneOrEmail(account, agentId);
    }

    /**
     * 获取客户详情
     * @date 2021/11/3
     * @param
     * @return com.zhongzhi.data.api.ApiResult<com.zhongzhi.data.vo.CustomerQueryVo>
     */
    public ApiResult<CustomerQueryVo> getCustomer() throws Exception {
        Customer customer = ThreadLocalContainer.getCustomer();
        CustomerQueryVo customerQueryVo = this.getCustomerWithEmptyAccountInfo(customer.getId());
        // this.addRealtimeAccountInfo(Collections.singletonList(customerQueryVo),
        //         Arrays.asList(CustomerConsume.ConsumeType.DEDUCTION_SUCCESS, CustomerConsume.ConsumeType.FREEZE));

        return ApiResult.ok(customerQueryVo);
    }

    /**
     * 个人中心-修改手机号-发送验证码
     * @date 2021/11/4
     * @param phone
     * @param response
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> sendSms(String phone, HttpServletResponse response) {
        // 1.校验一分钟内该手机号没有发送过短信。（修改手机号）
        ApiResult smsResult = checkSendAgain(phone);
        if (!smsResult.isOk()) {
            return smsResult;
        }

        HttpServletRequest request = RequestUtil.getRequest();
        String domain = request.getHeader("domain");
        
        // 2.发送短信
        // 2.1 生成短信内容
        String validSmsCode = RandomUtil.randomNumbers(6);
        String verifyToken = UUIDTool.getInstance().getUUID();

        // --- 获取代理商签名
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings==null || StringUtils.isBlank(agentSettings.getSmsSignature())) {
            logger.info("代理商状态异常或短信签名不正确, 域名: {}", domain);
            return ApiResult.fail("代理商状态异常或短信签名不正确，请联系客服进行处理，谢谢！");
        }
        String content = String.format("【%s】您的验证码是：%s", agentSettings.getSmsSignature(), validSmsCode);

        // 2.2 调用接口，发送短信
        logger.info("短信接口调用成功，手机号：{}，内容：{}，verifyToken:{}。", phone, content, verifyToken);
        ApiResult apiResult = SmsUtil.sendMsg(phone, content);
        if (!apiResult.isOk()) {
            return apiResult;
        }

        // --- 设置验证码缓存和发送过验证码缓存，
        redisClient.set(RedisConstant.MOBILE_SMS_CODE_PREFIX+verifyToken, validSmsCode, Constant.TWO_MINUTES);
        redisClient.set(RedisConstant.MOBILE_SMS_CODE_FLAG_PREFIX+agentSettings.getAgentId()+"-"+phone, "used", Constant.ONE_MINUTES);

        return ApiResult.ok(verifyToken);
    }

    /**
     * 校验1分钟之内该手机号没有发送过短信
     * @date 2021/10/29
     * @param phone
     * @return com.zhongzhi.data.common.api.ApiResult
     */
    private ApiResult checkSendAgain(String phone) {
        String domain = RequestUtil.getRequest().getHeader("domain");
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings==null || StringUtils.isBlank(agentSettings.getSmsSignature())) {
            logger.info("代理商状态异常或短信签名不正确, 域名: {}", domain);
            return ApiResult.fail("代理商状态异常或短信签名不正确，请联系客服进行处理，谢谢！");
        }

        String s = redisClient.get(RedisConstant.MOBILE_SMS_CODE_FLAG_PREFIX+agentSettings.getAgentId()+"-"+phone);
        if (!StringUtils.isBlank(s)) {
            return ApiResult.fail("发送短信验证码频繁，请稍后再试");
        }
        return ApiResult.ok();
    }

    /**
     * 个人中心-修改手机号
     * @date 2021/11/4
     * @param param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> modifyMobile(FrontModifyMobileParam param) {
        Customer customer = ThreadLocalContainer.getCustomer();
        // 1.校验原手机号是否正确
        if (!param.getOldPhone().equals(customer.getPhone())) {
            return ApiResult.fail("原手机号输入不正确");
        }

        // 2.校验新手机号是否已经被注册
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        Customer customerTemp = this.findByPhone(param.getNewPhone(), agentSettings.getAgentId());
        if (customerTemp != null) {
            return ApiResult.fail("新手机号已被注册");
        }

        // 3.校验旧手机号和新手机号的验证码
        ApiResult oldResult = checkCode(param.getOldVerifyToken(), param.getOldCode());
        if (!oldResult.isSuccess()) {
            return oldResult;
        }

        ApiResult newResult = checkCode(param.getNewVerifyToken(), param.getNewCode());
        if (!newResult.isSuccess()) {
            return newResult;
        }

        // 4.修改数据入库
        customer.setPhone(param.getNewPhone());
        customer.setName(param.getNewPhone());
        String remark = customer.getRemark()==null ? "":customer.getRemark();
        customer.setRemark(remark + " 手机号由" + param.getOldCode() + "变更为" + param.getNewPhone() + ";");
        int i = this.update(customer);
        if (i<=0) {
            logger.error("客户id：{}，修改客户手机号失败。param:{}", ThreadLocalContainer.getCustomerId(), param);
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            logger.info("客户id：{}，修改客户手机号成功。param:{}", ThreadLocalContainer.getCustomerId(), param);

            // 5.登出
            ApiResult logoutResult = loginService.logout();
            if (!logoutResult.isSuccess()) {
                return logoutResult;
            }
            return ApiResult.ok(true, "修改成功，请重新登录");
        }
    }

    /**
     * 个人中心-修改手机号-校验短信验证码-提交表单
     * @date 2021/11/4
     * @param
     * @return void
     */
    public ApiResult checkCode(String verifyToken, String code) {
        String mobileKey = RedisConstant.MOBILE_SMS_CODE_PREFIX + verifyToken;
        ApiResult result = CommonCheckCode(mobileKey, code);
        if (result.isSuccess()) {
            redisClient.remove(mobileKey);
        }
        return result;
    }

    /**
     * 个人中心-忘记密码-校验验证码
     * 个人中心-修改手机号-校验验证码
     * 个人中心-忘记解压密码-校验验证码
     * @date 2021/11/4
     * @param
     * @return void
     */
    public ApiResult checkCodeForget(String verifyToken, String code) {
        String mobileKey = RedisConstant.MOBILE_SMS_CODE_PREFIX + verifyToken;
        // 忘记密码/修改手机号，不删除redis key
        return CommonCheckCode(mobileKey, code);
    }

    /**
     * 校验短信验证码
     * @date 2021/11/5
     * @param redisKey 根据不同redisKey作区分
     * @param code
     * @return com.zhongzhi.data.api.ApiResult
     */
    private ApiResult CommonCheckCode(String redisKey, String code) {
        // 校验
        String redisCode = redisClient.get(redisKey);
        if (StringUtils.isBlank(redisCode)) {
            return ApiResult.fail("短信验证码已过期或不正确");
        } else if (!redisCode.equalsIgnoreCase(code)) {
            return ApiResult.fail("短信验证码错误");
        }

        // 如果是忘记密码的校验验证码，就不要删除key
        return ApiResult.ok(true);
    }

    /**
     * 客户-查询（通过邮箱）
     * @date 2021/11/4
     * @param email
     * @return com.zhongzhi.data.entity.customer.Customer
     */
    public Customer findByEmail(String email, Long agentId) {
        return customerMapper.findByEmail(email, agentId);
    }

    /**
     * 个人中心-绑定邮箱
     * @date 2021/11/4
     * @param customerId
     * @param email
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> addEmail(Long customerId, String email) {
        // 校验邮箱
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        ApiResult<Boolean> emailResult = emailIsUsed(email, agentSettings.getAgentId());
        if (!emailResult.isSuccess()) {
            return emailResult;
        }

        // 修改数据库
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail(email);
        int i = this.update(customer);
        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(true);
        }
    }

    /**
     * 个人中心-绑定邮箱-校验邮箱
     * @date 2021/11/4
     * @param email
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> emailIsUsed(String email, Long agentId) {
        Customer customer = this.findByEmail(email, agentId);
        if (customer!=null) {
            return ApiResult.fail("修改邮箱失败，该邮箱已被使用");
        }
        return ApiResult.ok(true);
    }

    /**
     * 个人中心-添加密码-添加密码
     * @date 2021/11/4
     * @param frontPasswordParam
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> updateCustomerPassword(FrontPasswordParam frontPasswordParam) {
        if (!StringUtils.equals(frontPasswordParam.getNewPassword(), frontPasswordParam.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 校验密码是否已存在
        Customer customer = ThreadLocalContainer.getCustomer();
        if (!StringUtils.isBlank(customer.getPassword())) {
            return ApiResult.fail("密码已存在不能创建新密码，请刷新页面重试");
        }

        // 密码加密
        String salt = SaltUtil.generateSalt();
        String newPassword = PasswordUtil.encrypt(frontPasswordParam.getNewPassword(), salt);
        customer.setSalt(salt);
        customer.setPassword(newPassword);

        // 修改数据库
        int i = this.update(customer);
        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(true);
        }
    }

    /**
     * 个人中心-添加密码-校验密码
     * @date 2021/11/5
     * @param customerId
     * @param oldPassword
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> validOldPassword(Long customerId, String oldPassword) {
        Customer customer = this.findById(customerId);

        // 密码加密处理
        String salt = customer.getSalt();
        String encryptOldPassword = PasswordUtil.encrypt(oldPassword, salt);
        if (!customer.getPassword().equals(encryptOldPassword)) {
            return ApiResult.fail("旧密码验证失败，请重新输入密码");
        }
        return ApiResult.result(true);
    }

    /**
     * 个人中心-添加密码-修改密码
     * @date 2021/11/5
     * @param frontUpdatePasswordParam
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> updatePassword(FrontUpdatePasswordParam frontUpdatePasswordParam) {
        // 校验密码
        Customer customer = ThreadLocalContainer.getCustomer();
        String salt = customer.getSalt();
        String encryptOldPassword = PasswordUtil.encrypt(frontUpdatePasswordParam.getOldPassword(), salt);
        if (!customer.getPassword().equals(encryptOldPassword)) {
            return ApiResult.fail("旧密码验证失败，请重新输入密码");
        }

        if (!StringUtils.equals(frontUpdatePasswordParam.getNewPassword(), frontUpdatePasswordParam.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 密码加密
        String newPassword = PasswordUtil.encrypt(frontUpdatePasswordParam.getNewPassword(), salt);
        customer.setPassword(newPassword);

        // 修改数据库
        int i = this.update(customer);
        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(true);
        }
    }

    /**
     * 个人中心-忘记密码-提交表单
     * @date 2021/11/5
     * @param param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> forgetPassword(FrontForgetParam param) {
        // 校验验证码
        String mobileKey = RedisConstant.MOBILE_SMS_CODE_PREFIX + param.getVerifySmsToken();
        ApiResult result = CommonCheckCode(mobileKey, param.getCode());
        if (!result.isSuccess()) {
            return result;
        }

        // 修改数据库
        Customer customer = ThreadLocalContainer.getCustomer();
        String newPassword = PasswordUtil.encrypt(param.getPassword(), customer.getSalt());
        customer.setPassword(newPassword);
        int i = this.update(customer);
        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(true);
        }
    }

    /**
     * 个人中心-添加解压密码
     * @date 2021/11/20
     * @param param
     * @return ApiResult<Boolean>
     */
    public ApiResult<Boolean> addCustomerUnzipPassword(UnzipPwdParam param) {
        // 1.校验密码是否已存在
        Customer customer = ThreadLocalContainer.getCustomer();
        if (!StringUtils.isBlank(customer.getUnzipPassword())) {
            return ApiResult.fail("解压密码已存在不能创建新解压密码，请刷新页面重试");
        }

        // 2.修改数据库
        customer.setUnzipPassword(param.getUnzipPassword());
        int i = this.update(customer);
        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(true);
        }
    }

    /**
     * 个人中心-修改解压密码
     * @date 2021/11/20
     * @param param
     * @return ApiResult<Boolean>
     */
    public ApiResult<Boolean> updateUnzipPassword(UpdateUnzipPwdParam param) {
        // 1.校验旧密码
        Long customerId = ThreadLocalContainer.getCustomerId();
        Customer customer = this.findById(customerId);

        if (!StringUtils.isBlank(param.getOldUnzipPwd()) && !param.getOldUnzipPwd().equals(customer.getUnzipPassword())) {
            return ApiResult.fail("旧解压密码验证失败，请重新输入密码。");
        }

        // 2.判断是修改密码还是取消密码
        Customer customerTemp = new Customer();
        customerTemp.setId(customerId);
        int i;
        if (!StringUtils.isBlank(param.getNewUnzipPwd()) && !StringUtils.isBlank(param.getNewUnzipPwdRepeat())) {
            // 修改密码
            customerTemp.setUnzipPassword(param.getNewUnzipPwd());
            // 3.修改数据库
            i = this.update(customerTemp);

        } else {
            // 取消密码
            customerTemp.setUnzipPassword(null);
            i = this.setNull(customerTemp);
        }

        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(true);
        }

    }
}
