package com.zhongzhi.data.controller.xunlong;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.service.CountryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 国际码号
 * @author liuh
 * @date 2022年6月10日
 */
@Slf4j
@RestController
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RequestMapping("/front/country")
public class CountryController {

	@Autowired
	private CountryService countryService;

	@RequestMapping("/codeList")
	@ApiOperation(value = "国际编码列表", notes = "国际编码列表")
    public ApiResult codeList() {
	    return ApiResult.ok(countryService.findList());
	}
}
