package com.zhongzhi.data.service.customer;


import cn.hutool.core.util.StrUtil;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.RedisConstant;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.entity.customer.CustomerExt;
import com.zhongzhi.data.entity.customer.CustomerRecharge;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.mapper.customer.CustomerExtMapper;
import com.zhongzhi.data.param.CustomerExtParam;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.MailService;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.service.sys.SysUserService;
import com.zhongzhi.data.util.*;
import com.zhongzhi.data.vo.BusinessLicenseVo;
import com.zhongzhi.data.vo.IdCardIdentifyResultVo;
import com.zhongzhi.data.vo.MailVo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * <pre>
 * 客户认证信息 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Service
public class CustomerExtService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerExtService.class);

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerExtMapper customerExtMapper;

    @Autowired
    private CustomerExtService customerExtService;

    @Autowired
    private CustomerRechargeService customerRechargeService;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MailService mailService;

    @Value("${online.time}")
    private String onlineTime;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisClient redisClient;

    /**
     * 通过客户id查询客户认证信息
     * @date 2021/11/1
     * @param id
     * @return com.zhongzhi.data.entity.customer.CustomerExt
     */
    public CustomerExt findByCustomerId(Long id) {
        return customerExtMapper.findByCustomerId(id);
    }

    /**
     * 客户认证信息-新增
     * @date 2021/11/2
     * @param param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @Transactional
    public ApiResult<Boolean> addCustomerExt(CustomerExtParam param) {
        // 1.新客户认证信息入库
        CustomerExt customerExt = new CustomerExt();
        BeanUtils.copyProperties(param, customerExt);
        customerExt.setVersion(0);

        if (param.getId()!=null) {
            // 修改
            customerExt.setId(Long.valueOf(param.getId()));
            int i = customerExtMapper.update(customerExt);
            if (i<=0) {
                logger.error("客户id：{}，修改客户认证记录失败。customerExt:{}", ThreadLocalContainer.getCustomerId(), customerExt);
                throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), ApiCode.DAO_EXCEPTION.getMsg());
            } else {
                logger.info("客户id：{}，修改客户认证记录成功。customerExt:{}", ThreadLocalContainer.getCustomerId(), customerExt);
            }

        } else {
            // 新增
            customerExt.setId(snowflake.nextId());
            int i = customerExtMapper.save(customerExt);
            if (i<=0) {
                logger.error("客户id：{}，新增客户认证记录失败。customerExt:{}", ThreadLocalContainer.getCustomerId(), customerExt);
                throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), ApiCode.DAO_EXCEPTION.getMsg());
            } else {
                logger.info("客户id：{}，新增客户认证记录成功。customerExt:{}", ThreadLocalContainer.getCustomerId(), customerExt);
            }

        }

        // 2.客户信息修改认证类型
        Customer customerTemp = new Customer();
        customerTemp.setId(ThreadLocalContainer.getCustomerId());
        customerTemp.setCustomerType(param.getCustomerType());
        // 如果是第二次提交，设置状态为未认证。
        customerTemp.setState(0);

        int j = customerService.update(customerTemp);
        if (j<=0) {
            throw new BusinessException(ApiCode.DAO_EXCEPTION.getCode(), ApiCode.DAO_EXCEPTION.getMsg());
        }

        // 3.发送邮件
        Customer customer = ThreadLocalContainer.getCustomer();
        String emails = sysUserService.getEmailJoiningByAgentId(customer.getAgentId());
        String certifyType = customer.getCustomerType() == 1 ? "企业" : "个人";
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        String agentName = agentSettings.getAgentName();

        MailVo mailVo = new MailVo();
        mailVo.setTo(emails);
        mailVo.setSubject(agentName + "有用户提交认证，请及时处理");
        mailVo.setText(agentName + "有用户(" + StrUtil.hide(customer.getPhone(), 3, 9) + ")提交"
                + certifyType + "认证，请及时处理.");
        mailService.sendMail(mailVo);

        return ApiResult.ok();
    }

    /**
     * 上传营业执照图片，返回营业执照文字识别
     * @date 2021/11/3
     * @param multipartFile
     * @return com.zhongzhi.data.api.ApiResult<com.zhongzhi.data.param.CustomerExtParam>
     */
    public ApiResult<BusinessLicenseVo> businessLicenseUpload(MultipartFile multipartFile) throws Exception {
        logger.info("ContentType = {}, OriginalFilename = {}, Name = {}, Size = {}", multipartFile.getContentType(),
                multipartFile.getOriginalFilename(), multipartFile.getName(), multipartFile.getSize());
        // 1.文件上传
        String fileUploadPath = uploadPath + "businessLicense";
        String saveFileName = fileUpload(multipartFile, fileUploadPath);

        // 2.调创蓝OCR接口
        BusinessLicenseVo businessLicenseVo = ChuangLanOcrUtil.bussinessLicenseOcrByImageBytes(multipartFile.getBytes());
        if(businessLicenseVo == null) {
            return ApiResult.fail("文字识别失败");
        }

        // 3.校验营业执照认证次数
        int count = this.countByBusinessLicenseNumber(businessLicenseVo.getSocialCreditCode());
        if (count >= 20) {
            return ApiResult.fail("认证次数过多");
        }

        // 4.返回结果
        String fileAccessPath = "businessLicense/" + saveFileName;
        businessLicenseVo.setFileAccessPath(fileAccessPath);
        logger.info("fileAccessPath:{}", fileAccessPath);

        return ApiResult.ok(businessLicenseVo);
    }

    /**
     * 文件上传
     * @date 2021/11/3
     * @param multipartFile
     * @param fileUploadPath 文件上传路径
     * @return java.lang.String 返回保存的文件名称
     */
    private String fileUpload(MultipartFile multipartFile, String fileUploadPath) throws Exception {
        return UploadUtil.upload(fileUploadPath, multipartFile, originalFilename -> {
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                // 这里可自定义文件名称，比如按照业务类型/文件格式/日期
                String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS"));
                String fileName = dateString + "." + fileExtension;
                return fileName;
            });
    }

    /**
     * 客户认证信息-计数（通过社会信用代码）
     * @date 2021/11/3
     * @param socialCreditCode
     * @return int
     */
    private int countByBusinessLicenseNumber(String socialCreditCode) {
        return customerExtMapper.countByBusinessLicenseNumber(socialCreditCode);
    }

    /**
     * 上传身份证正反面图片，返回身份证文字识别结果
     * @date 2021/11/3
     * @param multipartFile
     * @param side
     * @return com.zhongzhi.data.api.ApiResult<com.zhongzhi.data.vo.IdCardIdentifyResultVo>
     */
    public ApiResult<CustomerExtParam> idCardUpload(MultipartFile multipartFile, String side) throws Exception {
        logger.info("ContentType = {}, OriginalFilename = {}, Name = {}, Size = {}", multipartFile.getContentType(),
                multipartFile.getOriginalFilename(), multipartFile.getName(), multipartFile.getSize());
        // 1.文件上传
        String fileUploadPath = uploadPath + "idCard";
        String saveFileName = fileUpload(multipartFile, fileUploadPath);

        // 2.调创蓝OCR接口
        IdCardIdentifyResultVo idCardIdentifyResultVo = ChuangLanOcrUtil.idCardOcrByImageBytes(multipartFile.getBytes(), side);
        if(idCardIdentifyResultVo == null) {
            return ApiResult.fail("文字识别失败，请手工输入");
        }

        // 3.返回结果
        String fileAccessPath = "idCard/" + saveFileName;
        logger.info("fileAccessPath:{}", fileAccessPath);
        idCardIdentifyResultVo.setFileAccessPath(fileAccessPath);

        return ApiResult.ok(idCardIdentifyResultVo);
    }

    /**
     * 客户是否可以进行检测
     * @date 2021/11/3
     * @param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    public ApiResult<Boolean> isPermission() {
        Customer customer = ThreadLocalContainer.getCustomer();
        if (customer != null) {
            // 一、客户为已认证。
            // 查询redis缓存，无缓存则查询mysql，如果为已认证，则可以进行检测。
            if (getAuthInfo(customer)) {
                return ApiResult.ok(true);
            }

            // 二、客户为未认证。校验各种情况，符合情况的也能够检测文件。
            // 1.老客户无需认证，直接通过。以上线时间为界。
            if (checkOldCustomer(customer)) {
                return ApiResult.ok(true);
            }

            // 2.查询客户认证等级，根据认证等级来区分是否可以进行检测。并更新缓存
            boolean result = checkByAuthLevel(customer);
            return ApiResult.ok(result);

        } else {
            return ApiResult.fail("未获取到用户信息");
        }
    }

    /**
     * 根据客户认证等级来判断是否能够检测文件
     * @date 2021/11/13
     * @param customer
     * @return boolean
     */
    private boolean checkByAuthLevel(Customer customer) {
        boolean result = false;
        Integer level = customer.getAuthenticationLimitLevel();
        if (level != null) {
            switch (level) {
                case 0:
                    // 无认证限制
                    result = true;
                    break;
                case 1:
                    // 付费认证限制
                    // - 从未充值的客户，不用认证，通过
                    CustomerRecharge customerRecharge = customerRechargeService.findByCustomerIdAndPayTypeNe(customer.getId(), 2);
                    if (customerRecharge == null) {
                        result = true;
                        break;
                    }

                    // - 首充之后，7天还未认证的客户，需要认证。
                    Date remindTime = DateUtils.addDay(customerRecharge.getCreateTime(), 7);
                    if (DateUtils.getLongTime(new Date())>DateUtils.getLongTime(remindTime)) {
                        result = false;
                    } else {
                        result = true;
                    }
                    break;
                case 2:
                    // 需认证限制
                    result = false;
                    break;
                default:
            }
        } else {
            logger.error("校验是否能检测，客户认证等级为null。customerId:{}", customer.getId());
            throw new BusinessException("客户认证等级为空。");
        }
        return result;
    }

    /**
     * 校验是否为老客户
     * @date 2021/11/13
     * @param customer
     * @return boolean
     */
    private boolean checkOldCustomer(Customer customer) {
        String[] timeSplit = onlineTime.split("-");
        Integer year = Integer.parseInt(timeSplit[0]);
        Integer month = Integer.parseInt(timeSplit[1]);
        Integer day = Integer.parseInt(timeSplit[2]);
        LocalDate localDate = LocalDate.of(year, month, day);
        if (localDate.isAfter(customer.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            // 更新redis缓存
            refreshRedisCache(customer.getId(), Constant.PERMIT);
            return true;
        }
        return false;
    }

    /**
     * 获取客户认证信息
     * @date 2021/11/13
     * @param customer
     * @return boolean
     */
    private boolean getAuthInfo(Customer customer) {
        String s = redisClient.get(RedisConstant.CUSTOMER_IS_PERMISSION+ customer.getId());
        Integer isAuth = null;
        if (!StringUtils.isBlank(s)) {
            isAuth = Integer.parseInt(s);
            if (isAuth.equals(Constant.PERMIT)) {
                // 已认证，直接返回true
                return true;
            }
        } else {
            // 查询mysql
            Customer customerTemp = customerService.findById(customer.getId());
            if (Customer.CustomerStatus.VERIFIED.getStatus().equals(customerTemp.getState())) {
                // 已认证，直接返回true，插入缓存。
                refreshRedisCache(customer.getId(), Constant.PERMIT);
                return true;
            }
            // 未认证
        }
        return false;
    }

    /**
     * 更新客户是否能检测信息缓存
     * @date 2021/11/13
     * @param customerId
     * @return void
     */
    private void refreshRedisCache(Long customerId, Integer isPermit) {
        redisClient.set(RedisConstant.CUSTOMER_IS_PERMISSION+customerId, isPermit +"");
    }
}
