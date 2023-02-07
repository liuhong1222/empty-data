package com.zhongzhi.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <pre>
 * 产品线管理
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ApiModel(value = "ProductGroup对象", description = "产品线管理")
public class ProductGroup {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "所属代理商编号")
    @NotNull(message = "所属代理商编号不能为空")
    private Long agentId;

    @ApiModelProperty(value = "所属代理商")
    private String agentName;

    @ApiModelProperty(value = "产品线名称")
    @NotBlank(message = "产品线名称不能为空")
    private String name;

    @ApiModelProperty(value = "icon图片地址")
    private String icon;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态，0：下架，1：上架")
    private Integer state;

    @ApiModelProperty(value = "审批状态，0：初始化，1：创建待审核，2：修改待审核，3：已审核，4：已驳回，5：已删除")
    private Integer applyState;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
