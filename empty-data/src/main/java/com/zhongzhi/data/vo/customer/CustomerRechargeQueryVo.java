package com.zhongzhi.data.vo.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 客户充值记录 查询结果对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "CustomerRechargeQueryVo对象", description = "客户充值记录查询参数")
public class CustomerRechargeQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "代理商编号")
    private Long agentId;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "充值套餐名称")
    private String goodsName;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "单价（元/条）")
    private String price;

    @ApiModelProperty(value = "充值条数")
    private Long rechargeNumber;

    @ApiModelProperty(value = "充值金额")
    private String paymentAmount;

    @ApiModelProperty(value = "充值类型，0：对公转账，1：支付宝扫码付，2：注册赠送，3：赠送, 4：对公支付宝转账，5：对私支付宝， 6：对私微信，7：对私转账")
    private Integer payType;

    @ApiModelProperty(value = "身份证名称")
    private String idCardName;

    @ApiModelProperty(value = "已认证企业名称")
    private String certifiedCompanyName;

    @ApiModelProperty(value = "产品类别，0：空号检测产品，1：实时检测产品")
    private Integer category;

    @ApiModelProperty(value = "期初余条")
    private Long openingBalance;

    @ApiModelProperty(value = "期末余条")
    private Long closingBalance;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "操作者名称")
    private String creatorName;

    public String getName() {
        if (StringUtils.isNotBlank(certifiedCompanyName)) {
            return certifiedCompanyName;
        } else if (StringUtils.isNotBlank(idCardName)) {
            return idCardName;
        } else {
            return "";
        }
    }

}
