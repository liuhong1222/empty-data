package com.zhongzhi.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <pre>
 * 营业执照 文字识别结果对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-11
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BusinessLicenseVo对象", description = "营业执照文字识别结果参数")
public class BusinessLicenseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社会信用代码")
    private String socialCreditCode;

    @ApiModelProperty(value = "组成形式")
    private String compositionForm;

    @ApiModelProperty(value = "经营范围")
    private String businessScope;

    @ApiModelProperty(value = "法人")
    private String legalPerson;

    @ApiModelProperty(value = "成立日期")
    private String establishmentDate;

    @ApiModelProperty(value = "注册资本")
    private String registeredCapital;

    @ApiModelProperty(value = "证件编号")
    private String idNo;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "单位名称")
    private String companyName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "有效期")
    private String validityTerm;

    @ApiModelProperty(value = "上传文件访问路径")
    private String fileAccessPath;
}