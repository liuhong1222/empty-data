package com.zhongzhi.data.controller.xunlong;

import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.agent.Agent;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.*;
import com.zhongzhi.data.service.agent.AgentService;
import com.zhongzhi.data.service.customer.CustomerExtService;
import com.zhongzhi.data.service.customer.CustomerService;
import com.zhongzhi.data.util.*;
import com.zhongzhi.data.vo.BusinessLicenseVo;
import com.zhongzhi.data.vo.login.LoginCustomerTokenVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户
 * @author xybb
 * @date 2021-11-02
 */
@RestController
@RequestMapping("/front/customer")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerExtService customerExtService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private PhoneMatchUtil phoneMatchUtil;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    /**
     * 上传营业执照图片，返回营业执照文字识别
     * @date 2021/11/2
     * @param multipartFile
     * @return com.zhongzhi.data.api.ApiResult<BusinessLicenseUploadVo>
     */
    @PostMapping("/businessLicenseUpload")
    @ApiOperation(value = "上传营业执照", notes = "上传营业执照", response = ApiResult.class)
    public ApiResult<BusinessLicenseVo> businessLicenseUpload(@RequestParam("img") MultipartFile multipartFile) throws Exception {
        return customerExtService.businessLicenseUpload(multipartFile);
    }

    /**
     * 上传身份证正反面图片，返回身份证文字识别结果
     */
    @PostMapping("/idCardUpload")
    @ApiOperation(value = "上传身份证正反面图片", notes = "上传身份证正反面图片", response = ApiResult.class)
    public ApiResult<CustomerExtParam> idCardUpload(@RequestParam("img") MultipartFile multipartFile, @RequestParam("side") String side) throws Exception {
        // 校验参数
        if (!"front".equalsIgnoreCase(side) && !"back".equalsIgnoreCase(side)) {
            return ApiResult.result(ApiCode.PARAMETER_EXCEPTION);
        }
        return customerExtService.idCardUpload(multipartFile, side);
    }

    /**
     * 个人中心-修改手机号-发送验证码
     * 个人中心-忘记密码-发送验证码
     * @date 2021/11/4
     * @param smsCodeParam
     * @param response
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @PostMapping("/sendSms")
    @ApiOperation(value = "向注册手机号发送验证码", notes = "向注册手机号发送验证码", response = LoginCustomerTokenVo.class)
    public ApiResult<Boolean> sendSms(@Valid @RequestBody SmsCodeParam smsCodeParam, HttpServletResponse response) throws Exception {
        // 校验手机号码格式
        Long phoneNumber = PhoneUtil.toPhone(smsCodeParam.getPhone());
        if (phoneNumber == null) {
            return ApiResult.fail("手机号码格式不正确");
        }

        return customerService.sendSms(smsCodeParam.getPhone(), response);
    }

    /**
     * 个人中心-修改手机号
     * @date 2021/11/4
     * @param param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @FrontAgent
    @PostMapping("/modifyMobile")
    @ApiOperation(value = "修改手机号", notes = "修改手机号", response = LoginCustomerTokenVo.class)
    public ApiResult<Boolean> modifyMobile(@Valid @RequestBody FrontModifyMobileParam param) {
        // 校验新旧手机号
        if (param.getNewPhone().equals(param.getOldPhone())) {
            return ApiResult.fail("新旧手机号不能相同");
        }
        return customerService.modifyMobile(param);
    }

    /**
     * 个人中心-校验短信验证码
     * @date 2021/11/4
     * @param verifyToken
     * @param code
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @GetMapping("/verifySmsCode/{verifyToken}/{code}")
    @ApiOperation(value = "验证短信验证码", notes = "验证短信验证码", response = LoginCustomerTokenVo.class)
    public ApiResult<Boolean> verifySmsCode(@PathVariable("verifyToken") String verifyToken, @PathVariable("code") String code) {
        return customerService.checkCodeForget(verifyToken, code);
    }

    /**
     * 个人中心-忘记密码-校验验证码
     * @date 2021/11/4
     * @param verifyToken
     * @param code
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @GetMapping("/checkCodePwd/{verifyToken}/{code}")
    @ApiOperation(value = "验证短信验证码", notes = "验证短信验证码", response = LoginCustomerTokenVo.class)
    public ApiResult checkCodePwd(@PathVariable("verifyToken") String verifyToken, @PathVariable("code") String code) {
        return customerService.checkCodeForget(verifyToken, code);
    }

    /**
     * 个人中心-忘记密码-提交表单
     * @date 2021/11/5
     * @param param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @FrontAgent
    @PostMapping("/forgetPassword")
    @ApiOperation(value = "忘记密码", notes = "忘记密码，添加新手机号", response = ApiResult.class)
    public ApiResult<Boolean> forgetPassword(@Valid @RequestBody FrontForgetParam param) throws Exception {
        return customerService.forgetPassword(param);
    }

    /**
     * 普通分片上传文件
     *
     * @param multipartFile 文件
     * @param customerId    客户id
     * @param chunk         当前分片id
     * @param chunkSize     分片大小
     * @param chunks        分片总数
     * @param md5           文件md5
     * @param name          文件名
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadMatching")
    @ApiOperation(value = "分片上传文件", notes = "分片上传文件", response = ApiResult.class)
    public ApiResult<Boolean> uploadMatching(@RequestParam("file") MultipartFile multipartFile,
                                             @RequestParam("customerId") long customerId,
                                             @RequestParam("chunkNumber") long chunk,
                                             @RequestParam("chunkSize") long chunkSize,
                                             @RequestParam("totalChunks") long chunks,
                                             @RequestParam("identifier") String md5,
                                             @RequestParam("fileRealName") String name) throws Exception {
        logger.info("ContentType = {}, OriginalFilename = {}, Name = {}, Size = {}", multipartFile.getContentType(),
                multipartFile.getOriginalFilename(), multipartFile.getName(), multipartFile.getSize());

        String fileExtension = FilenameUtils.getExtension(name);
        if (fileExtension == null || (!"txt".equals(fileExtension.toLowerCase())
                && !"xls".equals(fileExtension.toLowerCase())
                && !"csv".equals(fileExtension.toLowerCase()))) {
            logger.error("文件类型不允许, customerId: {}, 文件名称：{}", customerId, name);
            return ApiResult.result(ApiCode.FAIL, "文件类型不允许", null);
        }
        if (multipartFile.getSize() > 20 * 1024 * 1024) {
            return ApiResult.result(ApiCode.FAIL, "文件超过20M无法上传", null);
        }

        Customer customer = ThreadLocalContainer.getCustomer();
        if (customer == null) {
            logger.error("当前用户不存在{}", customerId);
            return ApiResult.result(false);
        }

        Agent agent = agentService.findById(customer.getAgentId());
        if (agent == null) {
            logger.error("{}代理商不存在{}", customerId, customer.getAgentId());
            return ApiResult.result(false);
        }

        String uploadPath = fileUploadPath + customerId + "/" + md5 + "/";

        // 上传文件，返回保存的文件名称
        String saveFileName = UploadUtil.upload(uploadPath, multipartFile, originalFilename ->
                name.substring(0, name.lastIndexOf(".")) + "_" + chunk + name.substring(name.lastIndexOf("."))
        );

        long uploadedSize = FileUtil.getFileSize(new File(uploadPath));
        // 上传成功之后，返回访问路径，请根据实际情况设置
        Map<String, Object> map = new HashMap<>(8);
        map.put("name", name);
        map.put("md5", md5);
        map.put("customerId", customerId);
        map.put("chunk", chunk + "/" + chunks);
        map.put("uploadedSize", uploadedSize);
        return ApiResult.ok(map);
    }

    /**
     * 普通分片文件合并
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("/mergeFileMatching")
    @ApiOperation(value = "合并分片上传的文件", notes = "合并分片上传的文件", response = ApiResult.class)
    public ApiResult<Boolean> mergeFileMatching(@Valid UploadQueryParam param) throws Exception {
        String customerId = ThreadLocalContainer.getCustomerId().toString();
        Map<String, Object> map = new HashMap<>(8);
        map.put("name", param.getFileName());
        map.put("md5Name", param.getMd5());
        map.put("customerId", customerId);

        Customer customer = ThreadLocalContainer.getCustomer();
        if (customer == null) {
            logger.error("当前用户不存在{}", customerId);
            return ApiResult.result(ApiCode.FAIL, map);
        }

        Agent agent = agentService.findById(customer.getAgentId());
        if (agent == null) {
            logger.error("{}代理商不存在{}", customerId, customer.getAgentId());
            return ApiResult.result(ApiCode.FAIL, map);
        }

        // 分片文件地址
        String tempUploadPath = fileUploadPath + customerId + "/" + param.getMd5() + "/";
        // 合并文件地址
        String uploadPath = fileUploadPath + customerId + "/merge/" + param.getMd5() + "/";

        File file = new File(tempUploadPath);
        long fileSize = FileUtil.getFileSize(file);
        if (fileSize==0) {
            return ApiResult.fail("文件上传失败");
        }

        int count = 0;
        while (!file.exists() || !file.isDirectory() || file.listFiles().length != param.getChunks()
                || param.getFileSize() != fileSize) {
            try {
                //设置暂停的时间 1 秒
                Thread.sleep(1 * 1000);
                count++;
                fileSize = FileUtil.getFileSize(file);
                logger.info("分片文件上传尚未完成，当前fileSize:{}, 源文件大小：{}。传的分片参数:{}，源文件分片总数:{}",
                        fileSize, param.getFileSize(), param.getChunks(), file.listFiles().length);
                // 10分钟后跳出循环
                if (count == 10 * 60) {
                    break;
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }

        String mergeFilePath = uploadPath + param.getFileName();
        logger.info("合并文件开始：" + mergeFilePath);
        String[] paths = new String[param.getChunks()];
        for (int i = 0; i < param.getChunks(); i++) {
            String tempFileName = param.getFileName().substring(0, param.getFileName().lastIndexOf(".")) + "_" + i +
                    param.getFileName().substring(param.getFileName().lastIndexOf("."));
            paths[i] = tempUploadPath + tempFileName;
        }

        if (FileUtil.mergeFiles(paths, mergeFilePath)) {
            logger.info("合并文件成功：" + mergeFilePath);
            // 删除md5文件夹下分片上传和合并的临时文件
            FileUtil.deleteDir(file.getAbsolutePath());


            return ApiResult.ok(map);
        }

        logger.error("合并分片上传文件失败");
        return ApiResult.result(ApiCode.FAIL, "文件上传失败", map);
    }

    /**
     * 下载匹配完成的文件
     *
     * @return
     */
    @PostMapping("/downloadMatchingFile")
    @ApiOperation(value = "下载号码匹配文件", notes = "下载号码匹配完成的文件", response = ApiResult.class)
    public ApiResult downloadMatchingFile(@RequestBody DownloadQueryParam param) {
        // 客户文件目录
        String customerDir = fileUploadPath + param.getCustomerId();
        String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        File excelFile = getFile(customerDir + "/merge/" + param.getMd5Excel());
        File txtFile = getFile(customerDir + "/merge/" + param.getMd5Txt());

        // 生成结果文件名
        String fileExtension = FilenameUtils.getExtension(excelFile.getName());
        fileExtension = fileExtension.equals("xls")? ".xls":".csv";
        String downloadName = "匹配结果_" + dateString + fileExtension;

        // 进行匹配
        Integer count = phoneMatchUtil.matchPhoneNumbers(excelFile, txtFile,
                new File(customerDir + "/matching/" + downloadName));

        // 删除md5文件夹下分片上传和合并的临时文件
        FileUtil.deleteDir(excelFile.getParentFile().getAbsolutePath());
        FileUtil.deleteDir(txtFile.getParentFile().getAbsolutePath());

        // 返回访问路径，请根据实际情况设置
        String fileAccessPath = param.getCustomerId() + "/matching/" + downloadName;
        logger.info("fileAccessPath:{}", fileUploadPath + fileAccessPath);

        Map<String, Object> map = new HashMap<>(8);
        map.put("name", downloadName);
        map.put("count", count);
        map.put("customerId", param.getCustomerId());
        map.put("fileAccessPath", fileAccessPath);

        return ApiResult.ok(map);
    }

    /**
     * 在客户目录获取文件
     * @date 2021/12/2
     * @param path
     * @return File
     */
    private File getFile(String path) {
        File file = new File(path);
        if (file != null && file.listFiles().length!=0) {
            return file.listFiles()[0];
        } else {
            logger.error("客户id{}，号码匹配-获取文件失败。path:{}", ThreadLocalContainer.getCustomerId(), path);
            throw new BusinessException("读取文件失败，请重试。");
        }
    }

}
