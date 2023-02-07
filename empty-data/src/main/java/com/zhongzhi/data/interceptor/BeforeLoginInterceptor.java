package com.zhongzhi.data.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BeforeLoginInterceptor extends BaseInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BeforeLoginInterceptor.class);

    private static final String accessControlAllowOrigin = "*";

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
        return InterceptorConstants.BEFORELOGIN;
    }

    @Override
    protected boolean process(HttpServletRequest request, HttpServletResponse response,Object handle) throws Exception {
        return true;
    }

    private void allowResponse(HttpServletResponse response) {
        String oldStr = response.getHeader("Access-Control-Allow-Headers");
        if(StringUtils.isBlank(oldStr)) {
            response.setHeader("Access-Control-Allow-Headers", "x-token,x-time,x-key");
        } else {
            if(!oldStr.contains("x-token,x-time,x-key")) {
                if(oldStr.endsWith(",")) {
                    response.setHeader("Access-Control-Allow-Headers", oldStr + "x-token,x-time,x-key");
                } else {
                    response.setHeader("Access-Control-Allow-Headers", oldStr + ",x-token,x-time,x-key");
                }
            }
        }
    }

}
