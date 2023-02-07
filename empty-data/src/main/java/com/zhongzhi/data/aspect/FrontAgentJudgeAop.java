/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhongzhi.data.aspect;


import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.util.HttpServletRequestUtil;
import com.zhongzhi.data.util.ThreadLocalContainer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>
 * Controller Aop
 * 判断用户登录的代理商网站是否是本人所属
 * </p>
 *
 * @author geekidea
 * @since 2019-10-23
 */
@Slf4j
@Aspect
@Component
public class FrontAgentJudgeAop {

    /**
     * 切点
     */
    private static final String POINTCUT = "@annotation(com.zhongzhi.data.annotation.FrontAgent)";

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Around(POINTCUT)
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法的签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();
        FrontAgent frontAgent = method.getAnnotation(FrontAgent.class);

        if (frontAgent != null) {
            // 获取代理商网站域名及设置
            String domain = HttpServletRequestUtil.getRequest().getHeader("domain");

            // 获取当前登录用户
            Customer customer = ThreadLocalContainer.getCustomer();
            if (customer == null) {
                return joinPoint.proceed();
            }

            // AgentSettings agentSettings = agentSettingsService.findByAgentId(customer.getAgentId());
            // if (agentSettings != null && !agentSettings.getDomain().equalsIgnoreCase(domain)) {
            //     String message = "系统检测到您上次的登录地址为 " + "http://" + agentSettings.getDomain() + " , 请前往该网址登陆";
            //     return ApiResult.result(ApiCode.UNAUTHORIZED_EXCEPTION, message, "http://" + agentSettings.getDomain());
            // }
        }

        return joinPoint.proceed();

    }

}
