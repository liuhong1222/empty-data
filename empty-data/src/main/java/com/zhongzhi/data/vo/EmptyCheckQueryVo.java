package com.zhongzhi.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 空号检测记录 查询结果对象
 * </pre>
 *
 * @author rivers
 * @since 2020-03-03
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "EmptyCheckQueryVo对象", description = "空号检测记录查询参数")
public class EmptyCheckQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "所属代理商编号")
    private Long agentId;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "实号包（条）")
    private Long realNumber;

    @ApiModelProperty(value = "沉默包（条）")
    private Long silentNumber;

    @ApiModelProperty(value = "空号包（条）")
    private Long emptyNumber;

    @ApiModelProperty(value = "风险包（条）")
    private Long riskNumber;

    @ApiModelProperty(value = "检测文件中无效号码（条）")
    private Long illegalNumber;

    @ApiModelProperty(value = "接口检测数（条）")
    private Long unknownNumber;

    @ApiModelProperty(value = "总条数；null表示未检测条数")
    private Long totalNumber;

    @ApiModelProperty(value = "客户上传检测号码文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "状态；-1：待分类号码数量太少，任务结束；-2：客户余额不足，-3：代理商余额不足，-4：待分类号码数量超过最大值，0：待扣款，1：扣款成功，2：缓存分析成功，3：调用检测接口发送检测号码文件成功，返回上传id，9：最终分类成功，任务结束，10；客户取消任务")
    private Integer status;

    @ApiModelProperty(value = "上传id")
    private String sendId;

    @ApiModelProperty(value = "接口返回检测条数")
    private String line;

    @ApiModelProperty(value = "检测接口类型：0 磬音（旧），1 磬音（新），2 创蓝")
    private Integer checkType;

    @ApiModelProperty(value = "产品类别，0：空号检测产品，1：实时检测产品")
    private Integer category;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "身份证名称")
    private String idCardName;

    @ApiModelProperty(value = "已认证企业名称")
    private String certifiedCompanyName;

    @ApiModelProperty(value = "上传文件查询接口重试次数")
    private Integer retryCount;

    @ApiModelProperty(value = "待上传检测条数")
    private Long sendCount;

    @ApiModelProperty(value = "是否是老数据。1：是，0：否")
    private Integer isOldData;

    @ApiModelProperty(value = "实号包路径")
    private String realFilePath;

    @ApiModelProperty(value = "空号包路径")
    private String emptyFilePath;

    @ApiModelProperty(value = "风险包路径")
    private String riskFilePath;

    @ApiModelProperty(value = "沉默包路径")
    private String silentFilePath;

    @ApiModelProperty(value = "压缩包名称")
    private String zipName;

    @ApiModelProperty(value = "压缩包路径")
    private String zipPath;

    @ApiModelProperty(value = "压缩包大小")
    private String zipSize;

    public String getCustomerName() {
        if (StringUtils.isNotBlank(certifiedCompanyName)) {
            return certifiedCompanyName;
        } else if (StringUtils.isNotBlank(idCardName)) {
            return idCardName;
        } else {
            return "";
        }
    }

}