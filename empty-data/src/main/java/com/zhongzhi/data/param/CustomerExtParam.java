package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 客户认证实体类
 * @author xybb
 * @date 2021-11-02
 */
@Data
@NoArgsConstructor
public class CustomerExtParam {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "客户编号")
    @NotNull(message = "客户编号不能为空")
    private Long customerId;

    @ApiModelProperty(value = "客户类型（1：企业，0：个人，9：其他）")
    @NotNull(message = "客户类型不能为空")
    private Integer customerType;

    /*
     * 企业
     */
    @ApiModelProperty(value = "营业执照地址")
    private String businessLicensePath;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "营业执照号")
    private String businessLicenseNumber;

    @ApiModelProperty(value = "公司地址")
    private String companyAddress;

    @ApiModelProperty(value = "企业法人")
    private String legalPerson;

    @ApiModelProperty(value = "营业执照有效期开始时间")
    private String businessLicenseExpireStartTime;

    @ApiModelProperty(value = "营业执照有效期结束时间")
    private String businessLicenseExpireEndTime;

    @ApiModelProperty(value = "经营范围")
    private String businessScope;

    /*
     * 个人
     */
    @ApiModelProperty(value = "身份证照片正面地址")
    private String idCardFrontPath;

    @ApiModelProperty(value = "身份证照片反面地址")
    private String idCardBackPath;

    @ApiModelProperty(value = "身份证号")
    private String idCardNumber;

    @ApiModelProperty(value = "身份证姓名")
    private String idCardName;

    @ApiModelProperty(value = "详细地址")
    private String idCardAddress;

    @ApiModelProperty(value = "身份证有效期开始时间")
    private String idCardExpireStartTime;

    @ApiModelProperty(value = "身份证有效期结束时间")
    private String idCardExpireEndTime;

    @ApiModelProperty(value = "上传文件访问路径")
    private String fileAccessPath;
}
