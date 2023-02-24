package com.zhongzhi.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * 国际检测查询记录
 * @author liuh
 * @date 2022年6月10日
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "InternationalCheckQueryVo对象", description = "国际检测记录查询参数")
public class InternationalCheckQueryVo implements Serializable {

	private static final long serialVersionUID = -2908131830084668302L;

	@ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    @ApiModelProperty(value = "已激活（条）")
    private Long activeNumber;

    @ApiModelProperty(value = "未激活（条）")
    private Long noRegisterNumber;
    
    @ApiModelProperty(value = "未注册（条）")
    private Long unknownNumber;

    @ApiModelProperty(value = "总条数（不含无效号码）；null表示未检测条数")
    private Long totalNumber;

    @ApiModelProperty(value = "逻辑删除，0：未删除，1：已删除")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "已激活包路径")
    private String activeFilePath;

    @ApiModelProperty(value = "未激活包路径")
    private String noRegisterFilePath;
    
    @ApiModelProperty(value = "未注册包路径")
    private String unknownFilePath;

    @ApiModelProperty(value = "压缩包名称")
    private String zipName;

    @ApiModelProperty(value = "压缩包路径")
    private String zipPath;

    @ApiModelProperty(value = "压缩包大小")
    private String zipSize;

    @ApiModelProperty(value = "检测状态")
    private Integer checkStatus;
    
    @ApiModelProperty(value = "检测进度")
    private String checkProcess;
}