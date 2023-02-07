package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <pre>
 * 客户充值记录 查询参数对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CustomerRechargeQueryParam对象", description = "客户充值记录查询参数")
public class FrontCustomerRechargeQueryParam extends PageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户Id")
    private Long customerId;

    @ApiModelProperty("充值类型。0：空号检测产品，1：实时检测产品 2-国际检测产品")
    private Integer category;

    /**
     * 创建时间的开始时间
     */
    @ApiModelProperty(value = "创建起始时间")
    private Date createTimeFrom;

    /**
     * 创建时间的终止时间
     */
    @ApiModelProperty(value = "创建终止时间")
    private Date createTimeEnd;
}
