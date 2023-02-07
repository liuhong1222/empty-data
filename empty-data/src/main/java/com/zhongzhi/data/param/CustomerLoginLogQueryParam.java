package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <pre>
 * 客户登录日志 查询参数对象
 * </pre>
 *
 * @author rivers
 * @date 2021-02-19
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CustomerLoginLogQueryParam对象", description = "客户登录日志查询参数")
public class CustomerLoginLogQueryParam extends PageParam {

    private static final long serialVersionUID = 13462364L;

    @NotNull(message = "客户ID不能为空")
    @ApiModelProperty("客户Id")
    private Long customerId;

    @ApiModelProperty("开始时间")
    private Date fromTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

}
