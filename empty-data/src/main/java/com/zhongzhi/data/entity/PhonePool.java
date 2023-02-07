package com.zhongzhi.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <pre>
 * 号码池
 * </pre>
 *
 * @author rivers
 * @since 2020-08-02
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PhonePool对象", description = "号码池")
public class PhonePool extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号码")
    private Long phone;

    @ApiModelProperty(value = "类型：0 活跃号，1 沉默号，2 风险号，3 空号")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

}
