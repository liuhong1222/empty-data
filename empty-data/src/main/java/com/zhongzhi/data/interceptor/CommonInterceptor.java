package com.zhongzhi.data.interceptor;


import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.entity.RequestDeviceInfo;
import com.zhongzhi.data.entity.agent.Agent;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.agent.AgentService;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.util.FrontTokenUtils;
import com.zhongzhi.data.util.RequestUtil;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.CustomerInfoVo;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 登录通用过滤器
 * @author liuh
 * @date 2021年3月8日
 */
@Component
public class CommonInterceptor extends BaseInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);
    private static final String TOKEN = "token";

    private static final String accessControlAllowOrigin = "*";

    @Autowired
    private RedisClient redisClient;
    
    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    protected void init(HttpServletRequest request, HttpServletResponse response, Object handler) {
        super.init(request, response, handler);
        // 统一处理跨域问题
        if (!"".equals(accessControlAllowOrigin)) {
            response.setHeader("Access-Control-Allow-Origin", accessControlAllowOrigin);
        }
        
        allowResponse(response);

    }

    @Override
    protected String interceptorName() {
        return InterceptorConstants.AUTHENTICATION;
    }

    @Override
    protected boolean process(HttpServletRequest request, HttpServletResponse response,Object handle) throws Exception {
        String token = request.getHeader(TOKEN);
        if(isSpecial(request)) {
            token = request.getParameter("token");
        }
        if (StringUtils.isEmpty(token)) {
            logger.error("preHandler - 请求非法，token不能为空");
            ApiResult.failToResponse(response, ApiCode.SYSTEM_TOKEN_EXPIRE, "请求非法，token不能为空");
            return false;
        }

        // 获取客户信息
        String phone = FrontTokenUtils.getPhone(token);
        Long agentId = FrontTokenUtils.getAgentId(token);
        String customerStr = redisClient.get(Constant.FRONT_LOGIN_USER + agentId + "-" + phone);
        Customer customer = null;
        if (!StringUtils.isBlank(customerStr)) {
            customer = JSONObject.parseObject(customerStr, Customer.class);
        }

        if (null == customer) {
            logger.error("preHandler - 请求非法，token已过期 - [请求参数:{}]", request.getParameterMap());
            ApiResult.failToResponse(response, ApiCode.SYSTEM_TOKEN_EXPIRE, "登录已过期，请重新登录");
            return false;
        }
        
        // 获取代理商信息
        Agent agent = agentService.findById(customer.getAgentId());
        if(agent == null) {
        	 logger.error("{}，请求失败，当前用户信息异常，代理商不存在，customer:{}", JSON.toJSONString(customer));
             ApiResult.failToResponse(response, ApiCode.FAIL, "当前用户信息异常");
             return false;
        }

        CustomerInfoVo customerInfoVo = new CustomerInfoVo();
        customerInfoVo.setAgentId(agent.getId());
        customerInfoVo.setCompanyName(agent.getCompanyName());
        customerInfoVo.setCustomerId(customer.getId());
        customerInfoVo.setCustomerName(customer.getName());
        customerInfoVo.setPhone(customer.getPhone());

        // 获取代理商设置信息
        AgentSettings agentSetting = getAgentSetting();

        ThreadLocalContainer.setCustomerId(customer.getId());
        ThreadLocalContainer.setCustomerInfo(customerInfoVo);
        ThreadLocalContainer.setCustomer(customer);
        ThreadLocalContainer.setAgentSettings(agentSetting);
        return true;
    }

    private void allowResponse(HttpServletResponse response) {
        String oldStr = response.getHeader("Access-Control-Allow-Headers");
        if(StringUtils.isBlank(oldStr)) {
            response.setHeader("Access-Control-Allow-Headers", "token");
        } else {
            if(!oldStr.contains("token")) {
                if(oldStr.endsWith(",")) {
                    response.setHeader("Access-Control-Allow-Headers", oldStr + "token");
                } else {
                    response.setHeader("Access-Control-Allow-Headers", oldStr + ",token");
                }
            }
        }
    }

    private boolean isSpecial(HttpServletRequest request) {        
        return false;
    }


    /**
     * 获取请求的真实IP地址
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取请求的设备信息
     * @param request
     * @return
     */
    public RequestDeviceInfo getDeviceInfo(HttpServletRequest request) {
        String agent=request.getHeader("User-Agent");
        if(StringUtils.isBlank(agent)) {
            return null;
        }

        //解析agent字符串
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        //获取浏览器对象
        Browser browser = userAgent.getBrowser();
        //获取操作系统对象
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        RequestDeviceInfo result = new RequestDeviceInfo();
        result.setBrowserName(browser.getName());
        result.setBrowserVersion(userAgent.getBrowserVersion());
        result.setOperatingSystemName(operatingSystem.getName());
        return result;
    }

    public AgentSettings getAgentSetting() {
        // 获取代理商网站域名及设置
        String domain = RequestUtil.getRequest().getHeader("domain");
        AgentSettings agentSettings = agentSettingsService.findByDomainAudited(domain);
        if (agentSettings == null) {
            return new AgentSettings();
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
}
