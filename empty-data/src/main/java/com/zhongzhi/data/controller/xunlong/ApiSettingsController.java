package com.zhongzhi.data.controller.xunlong;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.ApiSettings;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.service.ApiSettingsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外接口帐号管理
 * @author xybb
 * @date 2021-11-10
 */
@RestController
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RequestMapping("/front/apiSettings")
public class ApiSettingsController {

    @Autowired
    private ApiSettingsService apiSettingsService;

    @ApiOperation(value = "获取apiSettings详情", notes = "获取客户对外接口账号信息", response = ApiSettings.class)
    @GetMapping("/info/{customerId}")
    public ApiResult<ApiSettings> getInfo(@PathVariable Long customerId) {
        ApiSettings apiSettings = apiSettingsService.getInfo(customerId);
        return ApiResult.ok(apiSettings);
    }
}
