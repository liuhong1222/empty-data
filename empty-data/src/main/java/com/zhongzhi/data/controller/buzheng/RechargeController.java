package com.zhongzhi.data.controller.buzheng;

import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.FrontCustomerRechargeQueryParam;
import com.zhongzhi.data.service.customer.CustomerRechargeService;
import com.zhongzhi.data.vo.customer.CustomerRechargeQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 客户充值记录
 * @author xybb
 * @date 2021-11-04
 */
@RestController
@RequestMapping("/front/recharge")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@Api("充值 API")
public class RechargeController {

    @Autowired
    private CustomerRechargeService customerRechargeService;

    /**
     * 空号检测-客户充值记录分页列表
     */
    @PostMapping("/getEmptyRechargePageList")
    @ApiOperation(value = "获取CustomerRecharge分页列表", notes = "客户充值记录分页列表", response = CustomerRechargeQueryVo.class)
    public ApiResult<PageInfo<CustomerRechargeQueryVo>> getEmptyRechargePageList(@Valid @RequestBody FrontCustomerRechargeQueryParam param) throws Exception {
        PageInfo<CustomerRechargeQueryVo> info = customerRechargeService.getCustomerRechargePageListForFront(param);
        return ApiResult.ok(info);
    }

    /**
     * 实时检测-客户充值记录分页列表
     */
    @PostMapping("/getRealtimePageList")
    @ApiOperation(value = "获取实时检测CustomerRecharge分页列表", notes = "客户实时检测充值记录分页列表", response = CustomerRechargeQueryVo.class)
    public ApiResult<PageInfo<CustomerRechargeQueryVo>> getRealtimeRechargePageList(@Valid @RequestBody FrontCustomerRechargeQueryParam param) throws Exception {
        PageInfo<CustomerRechargeQueryVo> info = customerRechargeService.getCustomerRechargePageListForFront(param);
        return ApiResult.ok(info);
    }

}
