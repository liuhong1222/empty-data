package com.zhongzhi.data.entity.sys;

import com.zhongzhi.data.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Version;

import javax.validation.constraints.*;

/**
 * 系统用户实体类
 * @author xybb
 * @date 2021-10-28
 */
@Data
@NoArgsConstructor
public class SysUser extends BaseEntity {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Length(max = 20, message = "用户名长度超过最大值")
    private String username;

    @ApiModelProperty(value = "昵称")
    @Length(max = 20, message = "昵称长度超过最大值")
    private String nickname;

    @ApiModelProperty(value = "密码")
    @Length(max = 64, message = "密码长度超过最大值")
    private String password;

    @ApiModelProperty(value = "盐值")
    private String salt;

    @ApiModelProperty(value = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Length(max = 20, message = "密码长度超过最大值")
    private String phone;

    @ApiModelProperty(value = "性别，0：女，1：男，默认1")
    @Min(value = 0, message = "性别输入有误")
    @Max(value = 1, message = "性别输入有误")
    private Integer gender;

    @ApiModelProperty(value = "头像")
    @Length(max = 200, message = "头像长度超过最大值")
    private String head;

    @ApiModelProperty(value = "联系邮箱")
    @Length(max = 64, message = "联系邮箱长度超过最大值")
    private String email;

    @ApiModelProperty(value = "备注")
    @Length(max = 200, message = "备注长度超过最大值")
    private String remark;

    @ApiModelProperty(value = "状态，0：禁用，1：启用，2：锁定")
    @Max(value = 2, message = "状态值不正确")
    @Min(value = 0, message = "状态值不正确")
    private Integer state;

    @ApiModelProperty(value = "代理商id")
    private Long agentId;

    @ApiModelProperty(value = "角色id")
    @NotNull(message = "角色id不能为空")
    private Long roleId;

    @ApiModelProperty(value = "逻辑删除，0：未删除，1：已删除")
    @Null(message = "逻辑删除不用传")
    private Integer deleted;

    @ApiModelProperty(value = "版本")
    @Null(message = "版本不用传")
    @Version
    private Integer version;

}
