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
 * 实时检测记录 查询参数对象
 * </pre>
 *
 * @author rivers
 * @date 2021-01-18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RealtimeCheckQueryParam对象", description = "实时检测记录查询参数")
public class RealtimeCheckQueryParam extends PageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "代理商ID")
    private Long agentId;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    /**
     * 创建时间的开始时间
     */
    @ApiModelProperty(value = "创建起始时间")
    @NotNull(message = "起始时间不能为空")
    private Date createTimeFrom;

    /**
     * 创建时间的终止时间
     */
    @ApiModelProperty(value = "创建终止时间")
    @NotNull(message = "终止时间不能为空")
    private Date createTimeEnd;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty(value = "手机号码")
    private String phone;
}
