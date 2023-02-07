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
 * 登录
 * @author xybb
 * @date 2021-10-28
 */
@RestController
@RequestMapping("/front")
@Api("代理商网站登录控制器")
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
     * 验证码登录
     * @date 2021/10/28
     * @param frontLoginParam
     * @param response
     * @return com.zhongzhi.data.common.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    @Limiter(limitNum = 2, limitType = Limiter.LimitType.IP)
    @PostMapping("/login")
    @ApiOperation(value = "代理商网站登录", notes = "代理商网站登录", response = LoginCustomerTokenVo.class)
    public ApiResult<LoginCustomerTokenVo> login(@Valid @RequestBody FrontLoginParam frontLoginParam, HttpServletResponse response) throws Exception {
        // 校验手机号码格式
        Long phoneNumber = PhoneUtil.toPhone(frontLoginParam.getPhone());
        if (phoneNumber == null) {
            return ApiResult.fail(ApiCode.LOGIN_EXCEPTION, "手机号码格式不正确");
        }

        return loginService.login(frontLoginParam, response);
    }

    /**
     * 账号密码登录
     * @date 2021/10/28
     * @param loginParam
     * @param response
     * @return com.zhongzhi.data.common.api.ApiResult<com.zhongzhi.data.vo.LoginCustomerTokenVo>
     */
    @Limiter(limitNum = 2, limitType = Limiter.LimitType.IP)
    @PostMapping("/userlogin")
    @ApiOperation(value = "代理商网站用户登录", notes = "代理商网站用户登录", response = LoginCustomerTokenVo.class)
    public ApiResult<LoginCustomerTokenVo> userLogin(@Valid @RequestBody FrontUserLoginParam loginParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String domain = request.getHeader("domain");
        return loginService.userLogin(loginParam.getUsername(), loginParam.getPassword(), domain, response);
    }

    /**
     * 发送短信验证码
     * @date 2021/10/28
     * @param smsCodeParam
     * @param response
     * @return com.zhongzhi.data.common.api.ApiResult<java.lang.String>
     */
    @PostMapping("/sendCode")
    @ApiOperation(value = "发送短信验证码", notes = "发送短信验证码", response = String.class)
    @ApiResponses({@ApiResponse(code = 500, message = "参数校验错误")})
    public ApiResult<String> sendCode(@Valid @RequestBody SmsCodeParam smsCodeParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 校验手机号码格式
        Long phoneNumber = PhoneUtil.toPhone(smsCodeParam.getPhone());
        if (phoneNumber == null) {
            return ApiResult.fail(ApiCode.LOGIN_EXCEPTION, "手机号码格式不正确");
        }

        return loginService.sendSmsCode(smsCodeParam.getPhone(),response);
    }
    
    /**
     * 获取当前请求
     *
     * @return request
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 登出
     * @date 2021/11/2
     * @return com.zhongzhi.data.api.ApiResult
     */
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @PostMapping("/logout")
    public ApiResult logout() throws Exception {
        return loginService.logout();
    }

    /**
     * 获取代理商设置信息
     */
    @GetMapping("/siteInfo")
    @ApiOperation(value = "获取AgentSettings对象详情", notes = "查看代理商网站设置", response = AgentSettings.class)
    public ApiResult<AgentSettings> getAgentSettings() throws Exception {
        AgentSettings agentSettings = getAgentSetting();
        AgentSettingsVo vo = new AgentSettingsVo();
        BeanUtils.copyProperties(agentSettings, vo);
        vo.setId(agentSettings.getId().toString());
        vo.setAgentId(agentSettings.getAgentId().toString());

        // 公司简称
        Agent agent = agentService.findById(agentSettings.getAgentId());
        vo.setCompanyShortName(agent.getCompanyShortName());

        // 返回前删除隐私数据，避免泄漏。
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
     * 产品线管理分页列表
     */
    @PostMapping("/productGroup/getList")
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @ApiOperation(value = "获取ProductGroup分页列表", notes = "产品线管理分页列表", response = ProductGroup.class)
    public ApiResult<List<ProductGroup>> getProductGroupList() throws Exception {
        List<ProductGroup> list = productGroupService.listByCondition();
        return ApiResult.ok(list);
    }

    /**
     * 产品管理分页列表
     */
    @PostMapping("/product/getList")
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @ApiOperation(value = "获取Product分页列表", notes = "产品管理分页列表", response = Product.class)
    public ApiResult<List<Product>> getProductList(@Valid @RequestBody ProductQueryParam productQueryParam) {
        List<Product> list = productService.listByCondition(productQueryParam.getProductGroupId());
        return ApiResult.ok(list);
    }

    /**
     * 产品常见问题管理分页列表
     */
    @PostMapping("/faq/getList")
    @Interceptor(name = InterceptorConstants.AUTHENTICATION)
    @ApiOperation(value = "获取Faq分页列表", notes = "产品常见问题管理分页列表", response = Faq.class)
    public ApiResult<List<Faq>> getFaqList() throws Exception {
        List<Faq> list = faqService.list();
        return ApiResult.ok(list);
    }

    public AgentSettings getAgentSetting() {
        // 获取代理商网站域名及设置
        String domain = RequestUtil.getRequest().getHeader("domain");
        if (StringUtils.isBlank(domain)) {
            throw new BusinessException("获取域名信息失败");
        }
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings == null) {
            logger.error("获取网站信息-通过域名获取审核通过代理商设置信息失败。domain:{}", domain);
            throw new BusinessException("获取代理商设置信息失败");
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
     * 关于我们
     */
    @PostMapping("/aboutUs")
    @ApiOperation(value = "获取关于我们", notes = "获取关于我们", response = News.class)
    public ApiResult<News> getAboutUs(String domain) {
    	if(StringUtils.isBlank(domain)) {
    		return ApiResult.fail(ApiCode.PARAMETER_EXCEPTION, "域名不能为空");
    	}
        List<News> list = newsService.listByAgentIdAudited(domain);
        return ApiResult.ok(list != null && list.size() > 0 ? list.get(0) : null);
    }

    /**
     * 获取代理商号码魔方地址
     * @date 2021/11/17
     * @param
     * @return String
     */
    @PostMapping("/getMobileCubePath")
    @ApiOperation(value = "获取代理商号码魔方地址", notes = "获取代理商号码魔方地址", response = News.class)
    public String getMobileCubePath(String fileType) {
    	if(StringUtils.isBlank(fileType)) {
    		return "类型不能为空";
    	}
    	
        return agentService.getMobileCubePath(fileType);
    }

    /**
     * 手动刷新号码匹配缓存的方法
     * @date 2021/11/25
     * @param
     * @return String
     */
    @PostMapping("/flushMobileMatchCache")
    public void flushMobileMatchCache() {
        phoneMatchUtil.flushMobileMatchCache();
    }
}
