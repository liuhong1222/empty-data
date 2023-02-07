package com.zhongzhi.data.entity.customer;

import com.zhongzhi.data.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Version;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 客户实体类
 * @author xybb
 * @date 2021-10-28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Customer extends BaseEntity {

    @ApiModelProperty(value = "客户名称")
    @NotBlank(message = "客户名称不能为空")
    private String name;

    @ApiModelProperty(value = "代理商编号")
    @NotNull(message = "代理商编号不能为空")
    private Long agentId;

    @ApiModelProperty(value = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String phone;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "盐值")
    private String salt;

    @ApiModelProperty(value = "联系邮箱")
    private String email;

    @ApiModelProperty(value = "解压密码")
    private String unzipPassword;

    @ApiModelProperty(value = "客户类型（1：企业，0：个人，9：其他）")
    private Integer customerType;

    @ApiModelProperty(value = "referer")
    private String referer;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "审批状态，0：待审核，9：已认证，1：已驳回")
    private Integer state;

    @ApiModelProperty("IP")
    private String ip;

    @ApiModelProperty("区域")
    private String area;

    @ApiModelProperty("运营商")
    private String operator;

    @ApiModelProperty(value = "版本")
    @Version
    private Integer version;

    @ApiModelProperty(value = "认证限制等级")
    private Integer authenticationLimitLevel;

    public enum CustomerStatus {
        /**
         * 待审核
         */
        UNVERIFY(0),
        /**
         * 已认证
         */
        VERIFIED(9),
        /**
         * 已驳回
         */
        DISAGREE(1);

        private Integer status;

        CustomerStatus(Integer status) {
            this.status = status;
        }

        /**
         * 数值状态置为枚举类型
         *
         * @param status 数值状态
         * @return 枚举类型。若未匹配上，则返回null
         */
        @Nullable
        public static CustomerStatus toStatus(Integer status) {
            if (status == null) {
                return null;
            }

            for (CustomerStatus s : CustomerStatus.values()) {
                if (s.getStatus().equals(status)) {
                    return s;
                }
            }

            return null;
        }

        public Integer getStatus() {
            return status;
        }
    }
}
