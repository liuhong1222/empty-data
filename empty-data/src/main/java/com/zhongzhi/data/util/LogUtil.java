package com.zhongzhi.data.util;


import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.entity.IpAddress;
import com.zhongzhi.data.entity.RequestInfo;
import com.zhongzhi.data.entity.customer.CustomerLoginLog;
import com.zhongzhi.data.entity.customer.CustomerLoginLogService;
import com.zhongzhi.data.exception.SpringBootPlusException;
import com.zhongzhi.data.service.IpAddressService;
import com.zhongzhi.data.vo.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class LogUtil {
    /**
     * 零
     */
    private static final int ZERO = 0;
    /**
     * 截取字符串的最多长度
     */
    private static final int MAX_LENGTH = 300;
    /**
     * 登出路径
     */
    private static final String LOGOUT_PATH = "/front/logout";
    /**
     * 登录日志：登录类型
     */
    private static final int LOGIN_TYPE = 1;
    /**
     * 登录日志：登出类型
     */
    private static final int LOGOUT_TYPE = 2;

    @Resource
    private IpAddressService ipAddressService;

    @Resource
    private CustomerLoginLogService customerLoginLogService;
    /**
     * 项目上下文路径
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 获取实际路径
     *
     * @param requestPath
     * @return
     */
    private String getRealPath(String requestPath) {
        // 如果项目路径不为空，则去掉项目路径，获取实际访问路径
        // if (StringUtils.isNotBlank(contextPath)) {
        //     return requestPath.substring(contextPath.length());
        // }
        return requestPath;
    }

    /**
     * 异步保存系统登录日志
     *
     * @param customerId
     * @param result
     * @param exception
     */
    public void saveSysLoginLog(Long customerId, Object result, Exception exception) {
        try {
            // 获取当前的HttpServletRequest对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // HTTP请求信息对象
            RequestInfo requestInfo = new RequestInfo();

            // 请求路径 /api/search/add
            String path = request.getRequestURI();
            requestInfo.setPath(path);
            // 获取实际路径 /search/add
            String realPath = getRealPath(path);
            requestInfo.setRealPath(realPath);

            // IP地址
            String ip = IpUtil.getRequestIp();
            requestInfo.setIp(ip);
            requestInfo.setRequestId(UUIDTool.getInstance().getUUID());

            // 获取请求方式
            String requestMethod = request.getMethod();
            requestInfo.setRequestMethod(requestMethod);

            // 获取请求内容类型
            String contentType = request.getContentType();
            requestInfo.setContentType(contentType);

            // 判断控制器方法参数中是否有RequestBody注解
            requestInfo.setRequestBody(true);
            // 用户浏览器代理字符串
            requestInfo.setUserAgent(request.getHeader(Constant.USER_AGENT));
            String refererUrl = request.getHeader(Constant.REFERER);

            Integer type = null;
            // 判断是否是登录路径
            if (realPath.equals(LOGOUT_PATH)) {
                type = LOGOUT_TYPE;
            } else {
                type = LOGIN_TYPE;
            }

            // 保存登录登出日志
            CustomerLoginLog customerLoginLog = new CustomerLoginLog();
            customerLoginLog.setType(type);
            customerLoginLog.setCustomerId(customerId);
            customerLoginLog.setReferer(refererUrl);
            // 设置异常信息
            if (exception != null) {
                Integer errorCode = null;
                String exceptionMessage = exception.getMessage();
                if (StringUtils.isNotBlank(exceptionMessage)) {
                    exceptionMessage = StringUtils.substring(exceptionMessage, ZERO, MAX_LENGTH);
                }
                if (exception instanceof SpringBootPlusException) {
                    SpringBootPlusException customException = (SpringBootPlusException) exception;
                    errorCode = customException.getErrorCode();
                }
                // 异常字符串长度截取
                customerLoginLog.setCode(errorCode).setExceptionMessage(exceptionMessage);
            }

            // 判断登录登出结果
            if (result != null && result instanceof ApiResult) {
                ApiResult<?> apiResult = (ApiResult<?>) result;
                customerLoginLog.setSuccess(apiResult.isSuccess()).setCode(apiResult.getCode());
                if (!apiResult.isSuccess()) {
                    customerLoginLog.setExceptionMessage(apiResult.getMsg());
                }
            } else {
                customerLoginLog.setSuccess(false);
            }

            // 设置请求参数信息
            if (requestInfo != null) {
                customerLoginLog.setIp(requestInfo.getIp()).setRequestId(requestInfo.getRequestId());

                // User-Agent
                String userAgent = requestInfo.getUserAgent();
                if (StringUtils.isNotBlank(userAgent)) {
                    customerLoginLog.setUserAgent(StringUtils.substring(userAgent, ZERO, MAX_LENGTH));
                }
                ClientInfo clientInfo = ClientInfoUtil.get(userAgent);
                if (clientInfo != null) {
                    customerLoginLog.setBrowserName(clientInfo.getBrowserName())
                            .setBrowserVersion(clientInfo.getBrowserversion())
                            .setEngineName(clientInfo.getEngineName())
                            .setEngineVersion(clientInfo.getEngineVersion())
                            .setOsName(clientInfo.getOsName())
                            .setPlatformName(clientInfo.getPlatformName())
                            .setMobile(clientInfo.isMobile())
                            .setDeviceName(clientInfo.getDeviceName())
                            .setDeviceModel(clientInfo.getDeviceModel());
                }
                IpAddress ipAddress = requestInfo.getIpAddress();
                if (ipAddress == null) {
                    ipAddress = ipAddressService.getByIp(requestInfo.getIp());
                }
                if (ipAddress != null) {
                    customerLoginLog.setArea(ipAddress.getArea()).setOperator(ipAddress.getOperator());
                }
                // 保存登录日志
                customerLoginLogService.saveCustomerLoginLog(customerLoginLog);
                log.info("保存登录日志成功。客户id：{}，customerLoginLog:{}", customerLoginLog.getCustomerId(), customerLoginLog);
            }
        } catch (Exception e) {
            log.error("保存客户登录日志失败", e);
        }
    }
}
