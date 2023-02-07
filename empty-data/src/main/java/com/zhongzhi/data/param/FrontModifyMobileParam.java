package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("修改用户手机号入参")
public class FrontModifyMobileParam {
    @ApiModelProperty("旧手机号")
    @NotBlank(message = "旧手机号不能为空")
    private String oldPhone;

    @ApiModelProperty("旧手机号验证码令牌")
    @NotBlank(message = "旧手机号验证码令牌不能为空")
    private String oldVerifyToken;

    @ApiModelProperty("旧手机号验证码")
    @NotBlank(message = "旧手机号验证码不能为空")
    private String oldCode;

    @ApiModelProperty("新手机号")
    @NotBlank(message = "新手机号不能为空")
    private String newPhone;

    @ApiModelProperty("新手机验证码令牌")
    @NotBlank(message = "新手机验证码令牌不能为空")
    private String newVerifyToken;

    @ApiModelProperty("新手机验证码")
    @NotBlank(message = "新手机验证码不能为空")
    private String newCode;
}
