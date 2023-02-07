package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 解压密码表单
 * @author xybb
 * @date 2021-11-20
 */
@Data
@NoArgsConstructor
public class UnzipPwdParam {

    @ApiModelProperty("解压密码")
    @NotBlank(message = "解压密码不能为空")
    private String unzipPassword;

    @ApiModelProperty("重复解压密码")
    @NotBlank(message = "重复解压密码不能为空")
    private String unzipPasswordRepeat;

}
