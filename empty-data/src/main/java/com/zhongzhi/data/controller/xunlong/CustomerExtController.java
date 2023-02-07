package com.zhongzhi.data.controller.xunlong;

import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.CustomerExtParam;
import com.zhongzhi.data.service.customer.CustomerExtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 客户认证信息
 * @author xybb
 * @date 2021-11-02
 */
@RestController
@RequestMapping("/front/personal")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@Api("代理商网站个人中心 API")
public class CustomerExtController {

    @Autowired
    private CustomerExtService customerExtService;

    /**
     * 客户认证信息-新增
     * @date 2021/11/2
     * @param param
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @FrontAgent
    @PostMapping("/add")
    @ApiOperation(value = "添加CustomerExt对象", notes = "添加客户认证信息", response = ApiResult.class)
    public ApiResult<Boolean> addCustomerExt(@Valid @RequestBody CustomerExtParam param) throws Exception {
        return customerExtService.addCustomerExt(param);
    }

    /**
     * 客户是否可以进行检测
     */
    @GetMapping("/isPermission")
    @ApiOperation(value = "客户是否可以进行检测", notes = "true：可以，false：不可以，需认证", response = Boolean.class)
    public ApiResult<Boolean> isPermission() throws Exception {
        return customerExtService.isPermission();
    }
}
