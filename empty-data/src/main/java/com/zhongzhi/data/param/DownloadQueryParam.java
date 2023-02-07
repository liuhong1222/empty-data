package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <pre>
 * 号码匹配下载参数对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "DownloadQueryParam对象", description = "下载查询参数")
public class DownloadQueryParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Excel文件名")
//    @NotBlank(message = "需要匹配的excel文件名不能为空")
    private String nameExcel;

    @ApiModelProperty(value = "Txt文件名")
//    @NotBlank(message = "待匹配的号码txt文件名不能为空")
    private String nameTxt;

    @ApiModelProperty(value = "md5Excel")
    @NotBlank(message = "需要匹配的excel文件未上传")
    private String md5Excel;

    @ApiModelProperty(value = "md5Txt")
    @NotBlank(message = "待匹配的号码txt文件未上传")
    private String md5Txt;

    @ApiModelProperty(value = "客户id")
    @NotBlank(message = "客户id不能为空")
    private String customerId;
}
