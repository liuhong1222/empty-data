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
public class UpdateUnzipPwdParam {

    @ApiModelProperty("旧解压密码")
    private String oldUnzipPwd;

    @ApiModelProperty("新解压密码")
    private String newUnzipPwd;

    @ApiModelProperty("重复新解压密码")
    private String newUnzipPwdRepeat;

}
