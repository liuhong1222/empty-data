package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("文件分片上传状态入参实体")
@Getter
@Setter
public class UploadFileStatusParam {

    @ApiModelProperty("文件md5")
    private String md5;

    @ApiModelProperty("分片总数")
    private Integer chunks;

    @ApiModelProperty("文件真实名称")
    private String fileName;

    @ApiModelProperty("产品code")
    private Integer productCode;
}
