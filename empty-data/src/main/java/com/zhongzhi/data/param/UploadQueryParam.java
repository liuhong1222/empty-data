package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <pre>
 * 上传参数对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UploadQueryParam对象", description = "上传查询参数")
public class UploadQueryParam extends PageParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "md5")
    @NotBlank(message = "md5值不能为空")
    private String md5;

    @ApiModelProperty(value = "文件名称")
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @ApiModelProperty(value = "分片总数")
    @NotNull(message = "分片总数不能为空")
    private Integer chunks;

    @ApiModelProperty(value = "分片大小")
    private Integer chunkSize;

    @ApiModelProperty(value = "当前分片")
    private Integer chunkNumber;

    @ApiModelProperty(value = "文件大小")
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @ApiModelProperty(value = "客户id")
    private String customerId;
}
