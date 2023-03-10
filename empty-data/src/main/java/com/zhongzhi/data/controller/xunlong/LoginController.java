package com.zhongzhi.data.controller.xunlong;

import cn.hutool.core.io.FileUtil;
import com.zhongzhi.data.annotation.Limiter;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.Faq;
import com.zhongzhi.data.entity.News;
import com.zhongzhi.data.entity.Product;
import com.zhongzhi.data.entity.ProductGroup;
import com.zhongzhi.data.entity.agent.Agent;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.agent.AgentSettingsVo;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.FrontLoginParam;
import com.zhongzhi.data.param.FrontUserLoginParam;
import com.zhongzhi.data.param.ProductQueryParam;
import com.zhongzhi.data.param.SmsCodeParam;
import com.zhongzhi.data.service.FaqService;
import com.zhongzhi.data.service.NewsService;
import com.zhongzhi.data.service.ProductGroupService;
import com.zhongzhi.data.service.ProductService;
import com.zhongzhi.data.service.agent.AgentService;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.service.front.LoginService;
import com.zhongzhi.data.util.PhoneMatchUtil;
import com.zhongzhi.data.util.PhoneUtil;
import com.zhongzhi.data.util.RequestUtil;
import com.zhongzhi.data.vo.login.LoginCustomerTokenVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.List;

/**
 * ??????
 * @author xybb
 * @date 2021-10-28
 */
@RestController
@RequestMapping("/front")
@Api("??????????????????????????????")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private FaqService faqService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ProductGroupService productGroupService;

    @Autowired
    private AgentService agentService;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private PhoneMatchUtil phoneMatchUtil;

    /**
     * ???????????????
     * @date 2021/10/28
     * @param frontLoginParam
     * @param response
     * @return com.zhongzhi.data.common.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    @Limiter(limitNum = 2, limitType = Limiter.LimitType.IP)
    @PostMapping("/login")
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????", response = LoginCustomerTokenVo.class)
    public ApiResult<LoginCustomerTokenVo> login(@Valid @RequestBody FrontLoginParam frontLoginParam, HttpServletResponse response) throws Exception {
        // ????????????????????????
        Long phoneNumber = PhoneUtil.toPhone(frontLoginParam.getPhone());
        if (phoneNumber == null) {
            return ApiResult.fail(ApiCode.LOGIN_EXCEPTION, "???????????????????????????");
        }

        return loginService.login(frontLoginParam, response);
    }

    /**
     * ??????????????????
     * @date 2021/10/28
     * @param loginParam
     * @param response
     * @return com.zhongzhi.data.common.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    @Limiter(limitNum = 2, limitType = Limiter.LimitType.IP)
    @PostMapping("/userlogin")
    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????", response = LoginCustomerTokenVo.class)
    public ApiResult<LoginCustomerTokenVo> userLogin(@Valid @RequestBody FrontUserLoginParam loginParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String domain = request.getHeader("domain");
        return loginService.userLogin(loginParam.getUsername(), loginParam.getPassword(), domain, response);
    }

    /**
     * ?????????????????????
     * @date 2021/10/28
     * @param smsCodeParam
     * @param response
     * @return com.zhongzhi.data.common.api.ApiResult<java.lang.String>
     */
    @PostMapping("/sendCode")
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????", response = String.class)
    @ApiResponses({@ApiResponse(code = 500, message = "??????????????????")})
    public ApiResult<String> sendCode(@Valid @RequestBody SmsCodeParam smsCodeParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // ????????????????????????
        Long phoneNumber = PhoneUtil.toPhone(smsCodeParam.getPhone());
        if (phoneNumber == null) {
            return ApiResult.fail(ApiCode.LOGIN_EXCEPTION, "???????????????????????????");
        }

        return loginService.sendSmsCode(smsCodeParam.getPhone(),response);
    }
    
    /**
     * ??????????????????
     *
     * @return request
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * ??????
     * @date 2021/11/2
     * @return com.zhongzhi.data.api.ApiResult
     */
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @PostMapping("/logout")
    public ApiResult logout() throws Exception {
        return loginService.logout();
    }

    /**
     * ???????????????????????????
     */
    @GetMapping("/siteInfo")
    @ApiOperation(value = "??????AgentSettings????????????", notes = "???????????????????????????", response = AgentSettings.class)
    public ApiResult<AgentSettings> getAgentSettings() throws Exception {
        AgentSettings agentSettings = getAgentSetting();
        AgentSettingsVo vo = new AgentSettingsVo();
        BeanUtils.copyProperties(agentSettings, vo);
        vo.setId(agentSettings.getId().toString());
        vo.setAgentId(agentSettings.getAgentId().toString());

        // ????????????
        Agent agent = agentService.findById(agentSettings.getAgentId());
        vo.setCompanyShortName(agent.getCompanyShortName());

        // ?????????????????????????????????????????????
        return ApiResult.ok(vo.setAlipayAppid(null)
                .setAlipayGateway(null)
                .setAlipayNotify(null)
                .setAlipayPublicKey(null)
                .setApplicationPrivateKey(null)
                .setDeputySignature(null)
                .setCompanyChop(null)
                .setSmsSignature(null)
                .setContactCompanyAccount(null)
                .setContactCompanyAddress(null)
                .setContactCompanyBank(null)
                .setContactCompanyName(null)
                .setContactPhone(null)
                .setContactPostcode(null)
                .setWechatAppid(null)
                .setWechatAppsecret(null)
                .setWechatGateway(null)
                .setWechatKey(null)
                .setWechatMchid(null)
                .setWechatpayNotify(null)
                .setState(null)
                .setRemark(null)
                .setVersion(null)
                .setCreateTime(null)
                .setUpdateTime(null));
    }

    /**
     * ???????????????????????????
     */
    @PostMapping("/productGroup/getList")
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @ApiOperation(value = "??????ProductGroup????????????", notes = "???????????????????????????", response = ProductGroup.class)
    public ApiResult<List<ProductGroup>> getProductGroupList() throws Exception {
        List<ProductGroup> list = productGroupService.listByCondition();
        return ApiResult.ok(list);
    }

    /**
     * ????????????????????????
     */
    @PostMapping("/product/getList")
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @ApiOperation(value = "??????Product????????????", notes = "????????????????????????", response = Product.class)
    public ApiResult<List<Product>> getProductList(@Valid @RequestBody ProductQueryParam productQueryParam) {
        List<Product> list = productService.listByCondition(productQueryParam.getProductGroupId());
        return ApiResult.ok(list);
    }

    /**
     * ????????????????????????????????????
     */
    @PostMapping("/faq/getList")
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @ApiOperation(value = "??????Faq????????????", notes = "????????????????????????????????????", response = Faq.class)
    public ApiResult<List<Faq>> getFaqList() throws Exception {
        List<Faq> list = faqService.list();
        return ApiResult.ok(list);
    }

    public AgentSettings getAgentSetting() {
        // ????????????????????????????????????
        String domain = RequestUtil.getRequest().getHeader("domain");
        if (StringUtils.isBlank(domain)) {
            throw new BusinessException("????????????????????????");
        }
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings == null) {
            logger.error("??????????????????-????????????????????????????????????????????????????????????domain:{}", domain);
            throw new BusinessException("?????????????????????????????????");
        }

        String path = uploadPath + "/agreement";
        File file = new File(path + "/" + agentSettings.getAgentId() + ".txt");
        if (file.exists() && file.isFile()) {
            agentSettings.setAgreement(FileUtil.readUtf8String(file));
        }
        if (StringUtils.isBlank(agentSettings.getAgreement()) || "<p></p>".equalsIgnoreCase(agentSettings.getAgreement())) {
            agentSettings.setAgreement(null);
        }
        return agentSettings;
    }

    /**
     * ????????????
     */
    @PostMapping("/aboutUs")
    @ApiOperation(value = "??????????????????", notes = "??????????????????", response = News.class)
    public ApiResult<News> getAboutUs(String domain) {
    	if(StringUtils.isBlank(domain)) {
    		return ApiResult.fail(ApiCode.PARAMETER_EXCEPTION, "??????????????????");
    	}
        List<News> list = newsService.listByAgentIdAudited(domain);
        return ApiResult.ok(list != null && list.size() > 0 ? list.get(0) : null);
    }

    /**
     * ?????????????????????????????????
     * @date 2021/11/17
     * @param
     * @return String
     */
    @PostMapping("/getMobileCubePath")
    @ApiOperation(value = "?????????????????????????????????", notes = "?????????????????????????????????", response = News.class)
    public String getMobileCubePath(String fileType) {
    	if(StringUtils.isBlank(fileType)) {
    		return "??????????????????";
    	}
    	
        return agentService.getMobileCubePath(fileType);
    }

    /**
     * ???????????????????????????????????????
     * @date 2021/11/25
     * @param
     * @return String
     */
    @PostMapping("/flushMobileMatchCache")
    public void flushMobileMatchCache() {
        phoneMatchUtil.flushMobileMatchCache();
    }
}
