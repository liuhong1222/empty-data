package com.zhongzhi.data.service.front;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.RedisConstant;
import com.zhongzhi.data.entity.ApiSettings;
import com.zhongzhi.data.entity.IpAddress;
import com.zhongzhi.data.entity.agent.Agent;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.entity.customer.CustomerBalance;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.StateEnum;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.param.FrontLoginParam;
import com.zhongzhi.data.param.SmsCodeParam;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.ApiSettingsService;
import com.zhongzhi.data.service.ImageYzmService;
import com.zhongzhi.data.service.IpAddressService;
import com.zhongzhi.data.service.MailService;
import com.zhongzhi.data.service.agent.AgentService;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.service.customer.CustomerBalanceService;
import com.zhongzhi.data.service.customer.CustomerRechargeService;
import com.zhongzhi.data.service.customer.CustomerService;
import com.zhongzhi.data.service.sys.SysUserService;
import com.zhongzhi.data.util.*;
import com.zhongzhi.data.vo.MailVo;
import com.zhongzhi.data.vo.login.LoginCustomerTokenVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 登录实现类
 * @author xybb
 * @date 2021-10-28
 */
@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private IpAddressService ipAddressService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private CustomerRechargeService customerRechargeService;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CustomerBalanceService customerBalanceService;

    @Autowired
    private ApiSettingsService apiSettingsService;
    
    @Autowired
    private ImageYzmService imageYzmService;

    @Resource
    private LogUtil logUtil;

    /**
     * 短信验证码校验令牌
     */
    private static final String VERIFY_SMS_TOKEN = "verifySmsToken";

    private static final String BAIDU_OCPC_URL = "https://ocpc.baidu.com/ocpcapi/api/uploadConvertData";

    /**
     * 验证码登录
     * @date 2021/10/28
     * @param frontLoginParam
     * @return com.zhongzhi.data.common.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    @Transactional
    public ApiResult<LoginCustomerTokenVo> login(FrontLoginParam frontLoginParam, HttpServletResponse response) throws Exception {
        // 1.校验短信验证码
        ApiResult smsResult = checkSmsCode(frontLoginParam.getVerifySmsToken(), frontLoginParam.getCode());
        if (!smsResult.isOk()) {
            return smsResult;
        }

        // 2.新增/校验客户
        // 验证代理商状态已审核
        HttpServletRequest request = RequestUtil.getRequest();
        String domain = request.getHeader("domain");
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings == null) {
            logger.error("未查询到代理商设置信息。domain:{}", domain);
            throw new BusinessException(ApiCode.FAIL.getCode(), "代理商状态异常，请联系客服进行处理，谢谢！");
        }

        String phone = frontLoginParam.getPhone();
        Customer customer = customerService.findByPhone(phone, agentSettings.getAgentId());
        if (customer == null) {
            // 判断是否在同类型官网上已注册
            // 查询该类型官网的所有代理商看是否有记录
            String agentDomain = agentSettingsService.findAgentDomainByOfficialWeb(agentSettings.getOfficialWeb(), phone);
            if (StringUtils.isBlank(agentDomain)) {
                // 新建客户（注册）
                Customer newCustomer = insertNewCustomer(phone, agentSettings);
                customer = newCustomer;

            } else {
                // 跳转已注册代理商域名
                String message = "系统检测到您上次的登录地址为 " + "http://" + agentDomain + " , 请前往该网址登陆";
                return ApiResult.result(ApiCode.UNAUTHORIZED_EXCEPTION, message, "http://"+agentDomain);
            }

        } else {
            // 已存在客户，校验ip和域名。
            // 检查用户是否登录了所属代理商网站
            ApiResult result = checkSameDomain(customer,domain);
            if (!result.isSuccess()) {
                logUtil.saveSysLoginLog(customer.getId(), result, null);
                return result;
            }

            if (StringUtils.isBlank(customer.getIp()) || StringUtils.isBlank(customer.getArea())) {
                // IP地址
                String ip = IpUtil.getRequestIp();
                customer.setIp(ip);
                // 设置IP区域
                IpAddress ipAddress = ipAddressService.getByIp(ip);
                if (ipAddress != null) {
                    customer.setArea(ipAddress.getArea()).setOperator(ipAddress.getOperator());
                }
                customerService.update(customer);
            }

        }

        // 3.进行登录
        return proceedLogin(customer, response);
    }

    /**
     * 进行登录
     * @date 2021/11/1
     * @param customer
     * @param response
     * @return com.zhongzhi.data.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    private ApiResult<LoginCustomerTokenVo> proceedLogin(Customer customer, HttpServletResponse response) throws Exception {
        // 获取登录视图
        String phone = customer.getPhone();
        String token = FrontTokenUtils.createToken(customer.getName(), customer.getAgentId(), phone);

        // - 解压密码脱敏
        String unzipPwd = setUnknown(customer);
        customer.setUnzipPassword(unzipPwd);

        // 设置redis缓存
        redisClient.set(Constant.FRONT_LOGIN_TOKEN+customer.getAgentId()+"-"+phone, token, Constant.ONE_DAY);
        redisClient.set(Constant.FRONT_LOGIN_USER+customer.getAgentId()+"-"+phone, JSONObject.toJSONString(customer), Constant.ONE_DAY);

        // 设置token响应头及响应信息
        response.setHeader("token", token);
        logger.debug("登陆成功，username:{}", customer.getName());

        // 隐私保护
        LoginCustomerTokenVo loginCustomerTokenVo = new LoginCustomerTokenVo();
        loginCustomerTokenVo.setToken(token);
        customer.setIp(null).setArea(null).setPassword(null).setSalt(null);
        loginCustomerTokenVo.setCustomer(customer);

        ApiResult result = ApiResult.ok(loginCustomerTokenVo, "登录成功");
        logUtil.saveSysLoginLog(customer.getId(), result, null);
        return result;
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

    private ApiResult checkSameDomain(Customer customer,String domain) {
        // 获取代理商网站域名及设置
        AgentSettings agentSettings = agentSettingsService.findByAgentIdAudited(customer.getAgentId());
        if (agentSettings == null) {
            return ApiResult.fail("代理商状态异常，请联系客服进行处理，谢谢！");
        }

        // if (!agentSettings.getDomain().equalsIgnoreCase(domain)) {
        //     String message = "系统检测到您上次的登录地址为 " + "http://" + agentSettings.getDomain() + " , 请前往该网址登陆";
        //     return ApiResult.result(ApiCode.UNAUTHORIZED_EXCEPTION, message, "http://" + agentSettings.getDomain());
        // }
        return ApiResult.ok();
    }

    /**
     * 新增客户
     * @date 2021/11/1
     * @param phone
     * @return boolean
     */
    public Customer insertNewCustomer(String phone, AgentSettings agentSettings) throws Exception {
        // - 组装参数
        Customer customer;
        Customer newCustomer = new Customer();
        HttpServletRequest request = RequestUtil.getRequest();
        String refererUrl = request.getHeader(Constant.REFERER);
        newCustomer.setId(snowflake.nextId());
        newCustomer.setReferer(refererUrl);
        newCustomer.setAgentId(agentSettings.getAgentId());
        newCustomer.setPhone(phone);
        newCustomer.setCreateTime(new Date());
        newCustomer.setUpdateTime(newCustomer.getCreateTime());
        newCustomer.setCustomerType(9);
        newCustomer.setState(0);
        newCustomer.setVersion(0);
        newCustomer.setPassword("");
        newCustomer.setName(phone);

        String ip = IpUtil.getRequestIp();
        IpAddress ipAddress = ipAddressService.getByIp(ip);
        newCustomer.setIp(ip);
        if (ipAddress != null) {
            newCustomer.setArea(ipAddress.getArea());
            newCustomer.setOperator(ipAddress.getOperator());
        }

        // -- 客户认证等级
        Agent agent = agentService.findById(agentSettings.getAgentId());
        newCustomer.setAuthenticationLimitLevel(agent.getAuthenticationLimitLevel());

        // 1.客户表新增记录
        ApiResult saveResult = customerService.save(newCustomer);
        if (!saveResult.isSuccess()) {
            throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), ApiCode.DAO_EXCEPTION.getMsg());
        }

        // 2.余额表新增记录
        CustomerBalance customerBalance = new CustomerBalance();
        customerBalance.setCustomerId(newCustomer.getId());
        customerBalance.setEmptyCount(0L);
        customerBalance.setRealtimeCount(0L);
        customerBalance.setInternationalCount(0L);
        customerBalance.setDirectCommonCount(0L);
        customerBalance.setLineDirectCount(0L);
        customerBalance.setEmptyRechargeNum(0L);
        customerBalance.setEmptyRechargeMoney(new BigDecimal(0));
        customerBalance.setRealtimeRechargeNum(0L);
        customerBalance.setRealtimeRechargeMoney(new BigDecimal(0));
        customerBalance.setInternationalRechargeNum(0L);
        customerBalance.setInternationalRechargeMoney(new BigDecimal(0));
        customerBalance.setDirectCommonRechargeNum(0L);
        customerBalance.setDirectCommonRechargeMoney(new BigDecimal(0));
        customerBalance.setLineDirectRechargeNum(0L);
        customerBalance.setLineDirectRechargeMoney(new BigDecimal(0));
        
        customerBalance.setVersion(0);
        ApiResult balanceResult = customerBalanceService.save(customerBalance);
        if (!balanceResult.isSuccess()) {
            throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), ApiCode.DAO_EXCEPTION.getMsg());
        }

        // 3.对外接口帐号表新增记录
        saveApiSettings(newCustomer);

        customer = newCustomer;
        // 羊毛党限制规则
        registerRestrict(newCustomer, customerBalance);
        // 通知百度营销接口，发送请求
        baiduOcpcRequest(request, agentSettings.getBaiduocpcToken());
        // email通知代理商客服有新用户注册
        noticeByEmail(customer, agentSettings);
        return customer;
    }

    /**
     * 生成appId和appKey
     * @date 2021/11/15
     * @param newCustomer
     * @return void
     */
    public ApiResult saveApiSettings(Customer newCustomer) {
        // appId和appKey做幂等
        ApiSettings apiSettings = this.checkAppIdAppKey(System.currentTimeMillis());
        if (apiSettings == null) {
            throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), "临时异常，请重试。");
        }

        // 新增记录
        apiSettings.setCustomerId(newCustomer.getId());
        apiSettings.setState(0);
        apiSettings.setVersion(0);
        ApiResult apiResult = apiSettingsService.save(apiSettings);
        if (!apiResult.isSuccess()) {
            throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), ApiCode.DAO_EXCEPTION.getMsg());
        }
        return apiResult;
    }

    /**
     * 校验appId和appKey的幂等性
     * @date 2021/11/10
     * @param startTime 开始时间
     * @return void
     */
    public ApiSettings checkAppIdAppKey(Long startTime) {
        ApiSettings apiSettings = new ApiSettings();
        // 递归执行时间不能过长
        long endTime = System.currentTimeMillis();
        if (endTime-startTime>3000) {
            return null;
        }

        // 校验appId
        String appId = RandomUtil.randomString(8);
        String appKey = RandomUtil.randomString(8);
        apiSettings.setAppId(appId);
        ApiSettings apiSettingsAppId = apiSettingsService.findByCondition(apiSettings);
        if (apiSettingsAppId != null) {
            return checkAppIdAppKey(startTime);
        }

        // 校验appKey
        apiSettings.setAppId(null);
        apiSettings.setAppKey(appKey);
        ApiSettings apiSettingsAppKey = apiSettingsService.findByCondition(apiSettings);
        if (apiSettingsAppKey != null) {
            return checkAppIdAppKey(startTime);
        }
        apiSettings.setAppId(appId);
        return apiSettings;
    }

    /**
     * 校验短信验证码
     * @date 2021/10/28
     * @param token
     * @param code
     * @return com.zhongzhi.data.common.api.ApiResult
     */
    private ApiResult checkSmsCode(String token, String code) {
        String redisKey = RedisConstant.LOGIN_SMS_CODE_PREFIX+token;
        String redisCode = redisClient.get(redisKey);
        if (StringUtils.isBlank(redisCode)) {
            return ApiResult.fail("验证码已失效，请重试");
        } else if (!redisCode.equalsIgnoreCase(code)) {
            return ApiResult.fail("短信验证码错误");
        }

        // 验证码校验成功，删除redis缓存
        redisClient.remove(redisKey);
        return ApiResult.ok();
    }

    /**
     * 发送短信验证码
     * @date 2021/10/29
     * @param phone
     * @return com.zhongzhi.data.common.api.ApiResult<java.lang.String>
     */
    public ApiResult<String> sendSmsCode(SmsCodeParam smsCodeParam, HttpServletResponse response) {
    	// 0.图文验证码校验
    	ApiResult iyzmResult = imageYzmService.invokeImageYzm(smsCodeParam.getIp(), smsCodeParam.getRandStr(), smsCodeParam.getTicket());
    	if (!iyzmResult.isOk()) {
    		logger.error("{},登录验证码发送失败，图文验证失败，",smsCodeParam.getPhone());
            return iyzmResult;
        }
    	
        // 1.校验一分钟内该手机号没有发送过短信
        ApiResult smsResult = checkSendAgain(smsCodeParam.getPhone());
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
            logger.error("代理商状态异常或短信签名不正确, 域名: {}", domain);
            return ApiResult.fail("代理商状态异常或短信签名不正确，请联系客服进行处理，谢谢！");
        }
        String content = String.format("【%s】您的验证码是：%s", agentSettings.getSmsSignature(), validSmsCode);

        // 2.2 调用接口，发送短信
        logger.info("短信接口调用成功，手机号：{}，内容：{}，verifyToken:{}。", smsCodeParam.getPhone(), content, verifyToken);
        ApiResult apiResult = SmsUtil.sendMsg(smsCodeParam.getPhone(), content);
        if (!apiResult.isOk()) {
            return apiResult;
        }
        // --- 设置验证码缓存和发送过验证码缓存，
        redisClient.set(RedisConstant.LOGIN_SMS_CODE_PREFIX+verifyToken, validSmsCode, Constant.TWO_MINUTES);
        redisClient.set(RedisConstant.LOGIN_SMS_CODE_FLAG_PREFIX+agentSettings.getAgentId()+"-"+smsCodeParam.getPhone(), "used", Constant.ONE_MINUTES);

        // 4.返回响应数据
        response.setHeader(VERIFY_SMS_TOKEN, verifyToken);
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
        String s = redisClient.get(RedisConstant.LOGIN_SMS_CODE_FLAG_PREFIX+agentSettings.getAgentId()+"-"+phone);
        if (!StringUtils.isBlank(s)) {
            return ApiResult.fail("发送短信验证码频繁，请稍后再试");
        }
        return ApiResult.ok();
    }

    private void registerRestrict(Customer customer, CustomerBalance customerBalance) throws Exception {
        Agent agent = agentService.findById(customer.getAgentId());
        if (StateEnum.ENABLE.getCode().equals(agent.getRegisterGift())) {
            // 手机号段规则
//            if (customer.getPhone().startsWith("165")
//                    || customer.getPhone().startsWith("170")
//                    || customer.getPhone().startsWith("171")
//                    || customer.getPhone().startsWith("167")
//                    || customer.getPhone().startsWith("162")) {
//                customerService.updateCustomer(customer.setRemark("疑似羊毛党:虚拟号段;"));
//                log.warn("疑似羊毛党:虚拟号段, customerId: {}, phone: {}", customer.getId(), customer.getPhone());
//                return;
//            }

            // 同IP规则
            int countIp = customerService.count(customer.getIp());
            if (countIp >= 2) {
                customer.setRemark("疑似羊毛党:相同IP;");
                customerService.update(customer);
                logger.warn("疑似羊毛党:相同IP, customerId: {}, IP: {}", customer.getId(), customer.getIp());
                return;
            }

            // 高频注册规则
            if (DateTime.now().isBeforeOrEquals(DateUtil.offsetHour(DateUtil.beginOfDay(DateTime.now()), 7))
                    && DateTime.now().isAfterOrEquals(DateUtil.offsetHour(DateUtil.beginOfDay(DateTime.now()), 0))) {
                DateTime before = DateUtil.offsetMinute(new Date(), -20);

                int countArea = customerService.countByAgentIdAndCreateTime(customer.getAgentId(), before.toJdkDate().toString());
                if (countArea >= 2) {
                    customer.setRemark("疑似羊毛党:相同IP;");
                    customerService.update(customer);
                    logger.warn("疑似羊毛党:超频注册, customerId: {}, phone: {}", customer.getId(), customer.getPhone());
                    return;
                }
            }

            // 赠送5000条
            customerRechargeService.saveRegisterGift(customer, null, customerBalance);
        } else {
            logger.info("代理商不存在或者未开启注册赠送。agentId:{}", customer.getAgentId());
        }
    }

    /*
     * 百度营销ocpc接口文档地址：
     * http://ocpc.baidu.com/developer/d/guide/?iurl=api%2Fapi-doc%2Fapi-interface%2F
     * 推广示例：
     * http://khkjc.com/promo/?s=bdpc&p=konghaojiance-danla-dkh-pc&u=konghaojiance&k=konghaojiance&e_adposition=cl2&e_keywordid=193891011389&sdclkid=A5f_152lb6DDxSDzxg&renqun_youhua=2476334&bd_vid=11395946760735774025
     * */
    private void baiduOcpcRequest(HttpServletRequest request, String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }

        // 获取请求是从哪里来的
        String referer = request.getHeader("referer");
        // 如果是百度营销推广地址
        if (referer != null && referer.contains("&bd_vid=")) {
            // 封装百度营销接口请求参数
            Map body = new HashMap();
            // 不同百度帐号的token不同
            body.put("token", token);

            Map itemMap = new HashMap<>();
            itemMap.put("newType", 49);
            itemMap.put("logidUrl", referer);
            List list = new ArrayList();
            list.add(itemMap);

            body.put("conversionTypes", list);

            String jsonStr = JSON.toJSONString(body);
            logger.info("百度营销接口请求参数：{}", jsonStr);
            String resp = HttpUtil.post(BAIDU_OCPC_URL, jsonStr);
            logger.info("百度营销接口返回：{}", resp);
        }
    }

    private void noticeByEmail(Customer customer, AgentSettings agentSettings) {
        String emails = sysUserService.getEmailJoiningByAgentId(agentSettings.getAgentId());
        //发送邮件
        MailVo mailVo = new MailVo();
        mailVo.setTo(emails);
        mailVo.setSubject(agentSettings.getAgentName() + "有新用户注册，请及时查看");
        mailVo.setText(agentSettings.getAgentName() + "新用户注册手机号码是：" + StrUtil.hide(customer.getPhone(), 3, 7) + ", 请及时查看");
        mailService.sendMail(mailVo);
    }

    /**
     * 登出
     * @date 2021/11/2
     * @return com.zhongzhi.data.api.ApiResult
     */
    public ApiResult logout() {
        // 获取手机号和客户id
        Long customerId = ThreadLocalContainer.getCustomerId();
        Customer customer = ThreadLocalContainer.getCustomer();
        String phone;
        if (customer != null) {
            phone = customer.getPhone();
            if (phone != null) {
                // 删除缓存信息
                redisClient.remove(Constant.FRONT_LOGIN_TOKEN+customer.getAgentId()+"-"+phone);
                redisClient.remove(Constant.FRONT_LOGIN_USER+customer.getAgentId()+"-"+phone);
                redisClient.remove(RedisConstant.CUSTOMER_VO_KEY+customerId);
                redisClient.remove(RedisConstant.CUSTOMER_EXT_KEY+customerId);

                // 保存登录日志
                ApiResult result = ApiResult.ok("退出成功");
                logUtil.saveSysLoginLog(customerId, result, null);
                return result;
            }
        }

        return ApiResult.fail();
    }

    /**
     * 账号密码登录
     * @date 2021/11/2
     * @param username
     * @param password
     * @return com.zhongzhi.data.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    public ApiResult<LoginCustomerTokenVo> userLogin(String username, String password, String domain,HttpServletResponse response) throws Exception {
        // 1.防止同ip恶意登录
        ApiResult loginResult = null;
        String ip = IpUtil.getRequestIp();
        Integer ipCount = (Integer) redisClient.getObject(Constant.CURRENT_LIMIT_KEY + ip);
        if (ipCount != null && ipCount >= 10) {
            logger.warn("存在恶意登录,IP: {}", ip);
            return ApiResult.fail("您的账号已锁定，请稍后重试");
        }

        // 2.校验代理商状态已审核
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings == null) {
            throw new BusinessException(ApiCode.FAIL.getCode(), "代理商状态异常，请联系客服进行处理，谢谢！");
        }

        // 3.校验账户名和密码
        Customer customer = customerService.selectByNameOrPhoneOrEmail(username.trim(), agentSettings.getAgentId());
        if (customer == null) {
            return ApiResult.fail("账户或密码不正确");
        }
        if (StringUtils.isBlank(customer.getPassword()) || StringUtils.isBlank(customer.getSalt())) {
            loginResult = ApiResult.fail("账户或密码不正确");
            logUtil.saveSysLoginLog(customer.getId(), loginResult, null);
            return loginResult;
        }

        // - 后台加密规则：sha256(sha256(123456) + salt)，前台加密规则：sha256(123456)
        String encryptPassword = PasswordUtil.encrypt(password, customer.getSalt());
        if (!encryptPassword.equals(customer.getPassword())) {
            loginResult = ApiResult.fail("账户或密码不正确");
            logUtil.saveSysLoginLog(customer.getId(), loginResult, null);
            return loginResult;
        }

        // 4.保存登录日志
        loginResult = ApiResult.ok("登录成功");
        logUtil.saveSysLoginLog(customer.getId(), loginResult, null);

        // 5.进行登录
        return proceedLogin(customer, response);
    }

}
