package com.zhongzhi.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 * 我的消息管理
 * </pre>
 *
 * @author rivers
 * @since 2020-02-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MyNotice对象", description = "我的消息管理")
public class MyNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    @ApiModelProperty(value = "通知编号")
    private Long noticeId;

    @ApiModelProperty(value = "消息标题")
    private String title;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "消息类别，0：系统消息，1：更新通知，2：活动通知，3：故障通知")
    private Integer noticeType;

    @ApiModelProperty(value = "是否已读，0：未读，1：已读")
    private Integer haveRead;

}
