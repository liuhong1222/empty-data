package com.zhongzhi.data.controller.xunlong;

import cn.hutool.core.lang.Validator;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.Goods;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.FrontPasswordParam;
import com.zhongzhi.data.param.FrontUpdatePasswordParam;
import com.zhongzhi.data.param.UnzipPwdParam;
import com.zhongzhi.data.param.UpdateUnzipPwdParam;
import com.zhongzhi.data.service.GoodsService;
import com.zhongzhi.data.service.customer.CustomerOrderRecordService;
import com.zhongzhi.data.service.customer.CustomerService;
import com.zhongzhi.data.util.IpUtil;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.customer.CustomerQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * 个人中心
 * @author xybb
 * @date 2021-11-03
 */
@RestController
@RequestMapping("/front/personal")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@Api("代理商网站个人中心 API")
public class CustomerPersonController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CustomerOrderRecordService customerOrderRecordService;

    /**
     * 获取客户详情
     */
    @GetMapping("/personalInfo")
    @ApiOperation(value = "获取Customer对象详情", notes = "查看客户管理", response = CustomerQueryVo.class)
    public ApiResult<CustomerQueryVo> getCustomer() throws Exception {
        return customerService.getCustomer();
    }

    /**
     * 个人中心-绑定邮箱-校验邮箱
     * @date 2021/11/4
     * @param email
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @PostMapping("/emailIsUsed/{email}")
    @ApiOperation(value = "验证邮箱地址是否被占用", notes = "验证邮箱地址是否被占用", response = Boolean.class)
    public ApiResult<Boolean> emailIsUsed(@PathVariable("email") String email) throws Exception {
        if (!Validator.isEmail(email)) {
            return ApiResult.fail("邮箱格式不正确");
        }
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        return customerService.emailIsUsed(email, agentSettings.getAgentId());
    }

    /**
     * 个人中心-绑定邮箱-新增邮箱
     * @date 2021/11/4
     * @param customerId
     * @param email
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @FrontAgent
    @PostMapping("/addEmail/{customerId}/{email}")
    @ApiOperation(value = "添加customer对象邮箱地址", notes = "添加客户邮箱地址", response = ApiResult.class)
    public ApiResult<Boolean> addCustomerEmail(@PathVariable("customerId") Long customerId,
                                               @PathVariable("email") String email) throws Exception {
        return customerService.addEmail(customerId, email);
    }

    /**
     * 个人中心-添加密码-添加密码
     * @date 2021/11/4
     * @param frontPasswordParam
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @FrontAgent
    @PostMapping("/addPassword")
    @ApiOperation(value = "添加customer对象密码", notes = "添加客户密码", response = ApiResult.class)
    public ApiResult<Boolean> addPassword(@Valid @RequestBody FrontPasswordParam frontPasswordParam) throws Exception {
        return customerService.updateCustomerPassword(frontPasswordParam);
    }

    /**
     * 个人中心-添加密码-校验密码
     * @date 2021/11/5
     * @param customerId
     * @param oldPassword
     * @return com.zhongzhi.data.api.ApiResult<java.lang.Boolean>
     */
    @PostMapping("/validOldPassword/{customerId}/{oldPassword}")
    @ApiOperation(value = "校验customer对象旧密码", notes = "校验客户旧密码", response = ApiResult.class)
    public ApiResult<Boolean> validOldPassword(@PathVariable("customerId") Long customerId,
                                               @PathVariable("oldPassword") String oldPassword) throws Exception {
        return customerService.validOldPassword(customerId, oldPassword);
    }

    @FrontAgent
    @PostMapping("/updatePassword")
    @ApiOperation(value = "更新customer对象密码", notes = "更新客户密码", response = ApiResult.class)
    public ApiResult<Boolean> updatePassword(@Valid @RequestBody FrontUpdatePasswordParam frontUpdatePasswordParam) throws Exception {
        if (frontUpdatePasswordParam.getNewPassword().equals(frontUpdatePasswordParam.getOldPassword())) {
            return ApiResult.fail("新密码和旧密码不能一致");
        }
        return customerService.updatePassword(frontUpdatePasswordParam);
    }

    /**
     * 获取套餐列表
     */
    @PostMapping("/goodsList")
    @ApiOperation(value = "获取goods对象列表", notes = "获取套餐列表", response = Goods.class)
    public ApiResult<Goods> getGoodsList() {
        return goodsService.getGoodsList();
    }

    /**
     * 获取支付宝支付二维码
     * @date 2021/11/8
     * @param id
     * @param amount
     * @return ApiResult<String>
     */
    @FrontAgent
    @GetMapping("/qrCodeString/{id}/{amount}")
    @ApiOperation(value = "获取支付宝支付二维码", notes = "获取支付宝支付二维码链接", response = String.class)
    public ApiResult<String> getAlipayQrCode(HttpServletRequest request,@PathVariable("id") Long id, @PathVariable("amount") BigDecimal amount, Integer payType) throws Exception {
        return customerOrderRecordService.getAlipayQrCode(id, amount,payType,IpUtil.getRequestIp(request));
    }

    /**
     * 获取支付宝扫码付订单状态
     * @date 2021/11/8
     * @param orderNo
     * @return ApiResult<String>
     */
    @GetMapping("/getQrCodePayState/{orderNo}")
    @ApiOperation(value = "获取支付宝扫码付订单成功状态", notes = "获取支付宝扫码付订单成功状态", response = String.class)
    public ApiResult<String> getQrCodePayState(@PathVariable("orderNo") String orderNo) {
        return customerOrderRecordService.getQrCodePayState(orderNo);
    }


    /**
     * 个人中心-添加解压密码
     */
    @FrontAgent
    @PostMapping("/addUnzipPassword")
    @ApiOperation(value = "添加解压密码", notes = "添加客户解压密码", response = ApiResult.class)
    public ApiResult<Boolean> addCustomerUnzipPassword(@Valid @RequestBody UnzipPwdParam param) {
        if (!StringUtils.equals(param.getUnzipPassword(), param.getUnzipPasswordRepeat())) {
            throw new BusinessException("两次输入的解压密码不一致");
        }
        return customerService.addCustomerUnzipPassword(param);
    }

    /**
     * 个人中心-修改解压密码
     */
    @FrontAgent
    @PostMapping("/updateUnzipPassword")
    @ApiOperation(value = "修改解压密码", notes = "修改客户解压密码", response = ApiResult.class)
    public ApiResult<Boolean> updateUnzipPassword(@Valid @RequestBody UpdateUnzipPwdParam param) {
        if (!StringUtils.isBlank(param.getNewUnzipPwd()) && !StringUtils.isBlank(param.getNewUnzipPwdRepeat())
        && !StringUtils.equals(param.getNewUnzipPwd(), param.getNewUnzipPwdRepeat())) {
            throw new BusinessException("两次输入的解压密码不一致");
        }
        return customerService.updateUnzipPassword(param);
    }

}
