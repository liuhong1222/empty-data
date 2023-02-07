package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * <pre>
 * 我的消息管理 查询参数对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MyNoticeQueryParam对象", description = "我的消息管理查询参数")
public class MyNoticeQueryParam extends PageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    @ApiModelProperty(value = "是否已读，0：未读，1：已读， null：全部")
    @Min(value = 0, message = "已读状态不正确")
    @Max(value = 1, message = "已读状态不正确")
    private Integer read;
}
