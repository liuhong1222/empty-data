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

package com.zhongzhi.data.exception;


import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.enums.ApiCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * @author geekidea
 * @since 2018-11-08
 */
@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 表单参数校验异常
     * @date 2021/3/15
     * @param methodArgumentNotValidException
     * @return com.zhongzhi.data.api.ApiResult
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult throwCustomException(MethodArgumentNotValidException methodArgumentNotValidException){
        // logger.error("[@Valid异常捕获] - \n" + ExceptionUtils.getStackTrace(methodArgumentNotValidException));
        return ApiResult.fail(methodArgumentNotValidException.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * SQL语法错误异常
     * @date 2021/3/19
     * @param e
     * @return com.chuanglan.pm.base.response.Result
     */
    @ResponseBody
    @ExceptionHandler(BadSqlGrammarException.class)
    public ApiResult error(BadSqlGrammarException e){
        logger.error("[SQL语法错误] - \n" + ExceptionUtils.getStackTrace(e));
        return ApiResult.fail(ApiCode.DAO_EXCEPTION);
    }

    /**
     * SQL语法错误异常
     * @date 2021/3/19
     * @param e
     * @return com.chuanglan.pm.base.response.Result
     */
    @ResponseBody
    @ExceptionHandler(MyBatisSystemException.class)
    public ApiResult error(MyBatisSystemException e){
        logger.error("[mybatis错误] - \n" + ExceptionUtils.getStackTrace(e));
        return ApiResult.fail(ApiCode.DAO_EXCEPTION);
    }

    /**
     * 系统登录异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = SysLoginException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult sysLoginExceptionHandler(SysLoginException exception) {
        log.warn("系统登录异常:" + exception.getMessage());
        return ApiResult.fail(ApiCode.LOGIN_EXCEPTION);
    }

    /**
     * HTTP解析请求参数异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("httpMessageNotReadableException:", exception);
        return ApiResult.fail(ApiCode.PARAMETER_EXCEPTION, ApiCode.PARAMETER_PARSE_EXCEPTION);
    }

    /**
     * HTTP
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = HttpMediaTypeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult httpMediaTypeException(HttpMediaTypeException exception) {
        log.error("httpMediaTypeException:", exception);
        return ApiResult.fail(ApiCode.PARAMETER_EXCEPTION, ApiCode.HTTP_MEDIA_TYPE_EXCEPTION);
    }

   /**
     * 自定义业务/数据异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {SpringBootPlusException.class})
    @ResponseStatus(HttpStatus.OK)
    public ApiResult springBootPlusExceptionHandler(SpringBootPlusException exception) {
        log.error("springBootPlusException: {}", exception.getMessage());
        int errorCode;
        // if (exception instanceof BusinessException) {
        //     errorCode = ApiCode.BUSINESS_EXCEPTION.getCode();
        // } else if (exception instanceof DaoException) {
        //     errorCode = ApiCode.DAO_EXCEPTION.getCode();
        // } else if (exception instanceof VerificationCodeException) {
        //     errorCode = ApiCode.VERIFICATION_CODE_EXCEPTION.getCode();
        // } else if (exception instanceof LimitException) {
        //     errorCode = ApiCode.LIMITER_EXCEPTION.getCode();
        // } else {
        //     errorCode = ApiCode.SPRING_BOOT_PLUS_EXCEPTION.getCode();
        // }
        return new ApiResult()
                .setCode(ApiCode.FAIL.getCode())
                .setMsg(exception.getMessage());
    }

    /**
     * 登陆授权异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult authenticationExceptionHandler(AuthenticationException exception) {
        log.error("authenticationException:", exception);
        return new ApiResult()
                .setCode(ApiCode.AUTHENTICATION_EXCEPTION.getCode())
                .setMsg(exception.getMessage());
    }

    /**
     * 未认证异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult unauthenticatedExceptionHandler(UnauthenticatedException exception) {
        log.error("unauthenticatedException:", exception);
        return ApiResult.fail(ApiCode.UNAUTHENTICATED_EXCEPTION);
    }

    /**
     * 未授权异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult unauthorizedExceptionHandler(UnauthorizedException exception) {
        log.error("unauthorizedException:", exception);
        return ApiResult.fail(ApiCode.UNAUTHORIZED_EXCEPTION);
    }

    /**
     * 默认的异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult exceptionHandler(Exception exception) {
        log.error("exception:", exception);
        return new ApiResult()
                .setCode(ApiCode.SYSTEM_EXCEPTION.getCode())
                .setMsg(exception.getMessage());
    }

}
