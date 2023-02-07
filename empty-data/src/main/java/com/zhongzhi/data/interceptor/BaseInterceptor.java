package com.zhongzhi.data.interceptor;

import com.zhongzhi.data.util.ThreadLocalContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 * @author liuh
 * @date 2021年3月8日
 */
abstract class BaseInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    /**
     * 请求前执行一些初始化操作，如果被继承最好也执行super方法
     *
     * @param request  Http Request
     * @param response Http Response
     * @param handler  handle
     */
    protected void init(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ThreadLocalContainer.setUUID();
    }

    /**
     * 请求开始统一处理
     *
     * @param request  Http Request
     * @param response Http Response
     * @param handler  handle
     * @return return
     * @throws Exception Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        init(request, response, handler);

        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        Interceptor interceptor=null;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.getMethod().isAnnotationPresent(Interceptor.class)) {
            interceptor = handlerMethod.getMethod().getAnnotation(Interceptor.class);
        } else if (handlerMethod.getBeanType().isAnnotationPresent(Interceptor.class)) {
            interceptor = handlerMethod.getBeanType().getAnnotation(Interceptor.class);
        }

        if (null != interceptor) {
            for (String name : interceptor.name()) {
                if (name.equals(interceptorName())) {
                    return process(request, response, handler);
                }
            }
        }

        return true;
    }

    /**
     * 请求结束统一处理
     *
     * @param request  Http Request
     * @param response Http Response
     * @param handler  handle
     * @param ex       Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            ThreadLocalContainer.clearAll();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 返回当前拦截器要处理的拦截器名称
     *
     * @return 当前拦截器要处理的拦截器名称
     */
    protected abstract String interceptorName();

    /**
     * 统一处理方法
     *
     * @param request  Http Request
     * @param response Http Response
     * @param handle   handle
     * @return return
     * @throws Exception Exception
     */
    protected abstract boolean process(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception;
}
