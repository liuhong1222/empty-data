package com.zhongzhi.data.controller.buzheng;

import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.CustomerExt;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.service.customer.CustomerExtService;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.customer.CustomerExtQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人信息
 * @author xybb
 * @date 2021-11-16
 */
@RestController
@RequestMapping("/front/personal")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
public class PersonController {

    @Autowired
    private CustomerExtService customerExtService;

    @FrontAgent
    @PostMapping("/getContractInfo")
    @ApiOperation(value = "获取联系方式", notes = "获取销售人、运维人联系方式信息", response = AgentSettings.class)
    public ApiResult<AgentSettings> getContractInfo() throws Exception {
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        return ApiResult.ok(agentSettings);
    }

    /**
     * 客户认证信息-查找
     * @date 2021/11/2
     * @param customerId
     * @return com.zhongzhi.data.api.ApiResult<com.zhongzhi.data.vo.CustomerExtQueryVo>
     */
    @GetMapping("/info/{customerId}")
    @ApiOperation(value = "获取CustomerExt对象详情", notes = "查看客户认证信息", response = CustomerExtQueryVo.class)
    public ApiResult<CustomerExtQueryVo> getCustomerExt(@PathVariable("customerId") Long customerId) throws Exception {
        CustomerExt customerExt = customerExtService.findByCustomerId(customerId);
        return ApiResult.ok(customerExt);
    }

    @FrontAgent
    @PostMapping("/getPaymentWay")
    @ApiOperation(value = "获取打款方式", notes = "获取对公、对私、微信、支付宝打款方式信息", response = AgentSettings.class)
    public ApiResult<AgentSettings> getPaymentWay() throws Exception {
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        return ApiResult.ok(agentSettings);
    }

}
