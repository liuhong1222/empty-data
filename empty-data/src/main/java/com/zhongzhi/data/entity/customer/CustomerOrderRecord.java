package com.zhongzhi.data.entity.customer;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <pre>
 * 客户支付宝支付交易记录
 * </pre>
 *
 * @author rivers
 * @since 2020-02-29
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ApiModel(value = "CustomerOrderRecord对象", description = "客户支付宝支付交易记录")
public class CustomerOrderRecord {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "所属代理商编号")
    @NotNull(message = "所属代理商编号不能为空")
    private Long agentId;

    @ApiModelProperty(value = "客户编号")
    @NotNull(message = "客户编号不能为空")
    private Long customerId;

    @ApiModelProperty(value = "充值套餐名称")
    @NotBlank(message = "充值套餐名称不能为空")
    private String goodsName;

    @ApiModelProperty(value = "充值套餐规格")
    private String goodsSpecifications;

    @ApiModelProperty(value = "充值套餐价格")
    private String goodsPrice;
    
    @ApiModelProperty(value = "支付方式  1-支付宝 8-微信")
    private Integer payType;

    @ApiModelProperty(value = "支付宝交易外部订单号")
    @NotBlank(message = "支付宝交易外部订单号不能为空")
    private String outOrderId;

    @ApiModelProperty(value = "购买者账号")
    private String buyerLogonId;

    @ApiModelProperty(value = "购买者UID")
    private String buyerUserId;

    @ApiModelProperty(value = "支付宝交易号")
    private String tradeNo;

    @ApiModelProperty(value = "支付宝交易状态")
    private Integer tradeStatus;

    @ApiModelProperty(value = "产品类别，0：空号检测产品，1：实时检测产品 2-国际检测产品")
//    @NotNull(message = "产品类别不能为空")
    @Range(min = 0, max = 1, message = "产品类别输入有误")
    private Integer category;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
