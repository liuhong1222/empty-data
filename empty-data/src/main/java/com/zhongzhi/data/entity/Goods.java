package com.zhongzhi.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <pre>
 * 充值套餐管理
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Goods对象", description = "充值套餐管理")
public class Goods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属代理商编号")
    @NotNull(message = "所属代理商编号不能为空")
    private Long agentId;

    @ApiModelProperty(value = "所属代理商")
    private String agentName;

    @ApiModelProperty(value = "充值套餐名称")
    @NotBlank(message = "充值套餐名称不能为空")
    private String name;

    @ApiModelProperty(value = "充值套餐单价（元/条）")
    @NotBlank(message = "充值套餐单价不能为空")
    private String unitPrice;

    @ApiModelProperty(value = "产品类别，0：空号检测产品，1：实时检测产品")
    @NotNull(message = "产品类别不能为空")
    @Range(min = 0, max = 1, message = "产品类别输入有误")
    private Integer category;

    @ApiModelProperty(value = "充值套餐类型，0：普通套餐，1：自定义套餐")
    @NotNull(message = "充值套餐类型不能为空")
    @Range(min = 0, max = 1, message = "充值套餐类型输入有误")
    private Integer type;

    @ApiModelProperty(value = "充值套餐条数")
    private String specifications;

    @ApiModelProperty(value = "充值套餐价格")
    private String price;

    @ApiModelProperty(value = "最低充值金额")
    private String minPayAmount;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "备注")
    @Length(max = 200, message = "备注长度超过200")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

}
