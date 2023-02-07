package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@ApiModel("大文件分片入参实体")
@Getter
@Setter
public class MultipartFileParam {

    @ApiModelProperty("文件真实名称")
    private String fileRealName;

    @ApiModelProperty("当前为第几分片")
    private Integer chunkNumber;

    @ApiModelProperty("每个分块的大小")
    private Long chunkSize;

    @ApiModelProperty("分片总数")
    private Integer totalChunks;

    @ApiModelProperty("文件唯一标识")
    private String identifier;

    @ApiModelProperty("分块文件传输对象")
    private MultipartFile file;

}
