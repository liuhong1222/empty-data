package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 忘记密码表单
 * @author xybb
 * @date 2021-11-05
 */
@Data
@ApiModel("忘记密码表单参数")
public class FrontForgetParam {

    @NotBlank(message = "请输入手机号码")
    @ApiModelProperty("手机号码")
    private String phone;

    @NotNull(message = "请输入短信验证码")
    @ApiModelProperty("短信验证码")
    private String code;

    @NotBlank(message = "请输入密码")
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("短信验证码Token")
    @NotBlank(message = "请输入号码")
    private String verifySmsToken;
}
