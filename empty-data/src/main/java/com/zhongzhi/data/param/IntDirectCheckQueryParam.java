package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 定向国际检测记录查询参数
 * @author liuh
 * @date 2022年10月18日
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "IntDirectCheckQueryParam对象", description = "定向国际检测记录查询参数")
public class IntDirectCheckQueryParam extends PageParam {
    @ApiModelProperty(value = "代理商ID")
    private Long agentId;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;
    
    @ApiModelProperty(value = "产品类型")
    private String productType;

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
}
