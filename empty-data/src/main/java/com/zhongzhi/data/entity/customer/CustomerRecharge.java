package com.zhongzhi.data.entity.customer;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <pre>
 * 客户充值记录
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ApiModel(value = "CustomerRecharge对象", description = "客户充值记录")
public class CustomerRecharge {

    private static final long serialVersionUID = 1675L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "代理商编号")
    private Long agentId;

    @ApiModelProperty(value = "客户编号")
    @NotNull(message = "客户编号不能为空")
    private Long customerId;

    @ApiModelProperty(value = "充值套餐名称")
    @NotNull(message = "充值套餐名称不能为空")
    private String goodsName;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String phone;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "单价（元/条）")
    private String price;

    @ApiModelProperty(value = "充值条数")
    @NotNull(message = "充值条数不能为空")
    @Min(value = 1, message = "最小充值条数无效")
    private Integer rechargeNumber;

    @ApiModelProperty(value = "充值金额")
    private String paymentAmount;

    @ApiModelProperty(value = "充值类型，0：对公转账，1：支付宝扫码付，2：注册赠送，3：赠送, 4：对公支付宝转账，5：对私支付宝， 6：对私微信，7：对私转账")
    private Integer payType;

    @ApiModelProperty(value = "产品类别，0：空号检测产品，1：实时检测产品")
//    @NotNull(message = "产品类别不能为空")
    @Range(min = 0, max = 1, message = "产品类别输入有误")
    private Integer category;

    @ApiModelProperty(value = "期初余条")
    private Long openingBalance;

    @ApiModelProperty(value = "期末余条")
    private Long closingBalance;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "操作者名称")
    private String creatorName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
