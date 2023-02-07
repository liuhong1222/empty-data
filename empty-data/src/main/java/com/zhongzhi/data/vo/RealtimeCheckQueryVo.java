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
 * 实时检测记录 查询结果对象
 * </pre>
 *
 * @author rivers
 * @date 2021-01-18
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "RealtimeCheckQueryVo对象", description = "实时检测记录查询参数")
public class RealtimeCheckQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "所属代理商编号")
    private Long agentId;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty(value = "客户编号")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "实号（条）")
    private Long normal;

    @ApiModelProperty(value = "空号（条）")
    private Long empty;

    @ApiModelProperty(value = "通话中（条）")
    private Long onCall;

    @ApiModelProperty(value = "在网但不可用（条）")
    private Long onlineButNotAvailable;

    @ApiModelProperty(value = "关机（条）")
    private Long shutdown;

    @ApiModelProperty(value = "疑似关机（条）")
    private Long suspectedShutdown;

    @ApiModelProperty(value = "停机（条）")
    private Long serviceSuspended;

    @ApiModelProperty(value = "携号转网（条）")
    private Long numberPortability;

    @ApiModelProperty(value = "号码错误（条）")
    private Long unknown;

    @ApiModelProperty(value = "未知（条）")
    private Long exceptionFailCount;

    @ApiModelProperty(value = "总条数（不含无效号码）；null表示未检测条数")
    private Long totalNumber;

    @ApiModelProperty(value = "客户上传文件地址")
    private String fileUrl;

    @ApiModelProperty(value = "状态；-1：号码数量为0；-2：客户余额不足，-3：代理商余额不足，0：待扣款，1：扣款成功，9：最终成功，10：用户已取消此任务")
    private Integer status;

    @ApiModelProperty(value = "第三方实时检测接口，0：创蓝")
    private Integer checkType;

    @ApiModelProperty(value = "接口重试次数")
    private Integer retryCount;

    @ApiModelProperty(value = "逻辑删除，0：未删除，1：已删除")
    private Integer deleted;

    @ApiModelProperty(value = "文件md5校验值")
    private String md5;

    @ApiModelProperty(value = "接口返回检测数")
    private String line;

    @ApiModelProperty(value = "已去重待检测数")
    private Long sendCount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "身份证名称")
    private String idCardName;

    @ApiModelProperty(value = "已认证企业名称")
    private String certifiedCompanyName;

    @ApiModelProperty(value = "是否是老数据。1：是，0：否")
    private Integer isOldData;

    @ApiModelProperty(value = "正常包路径")
    private String normalFilePath;

    @ApiModelProperty(value = "空号包路径")
    private String emptyFilePath;

    @ApiModelProperty(value = "通话中包路径")
    private String oncallFilePath;

    @ApiModelProperty(value = "不在网包路径")
    private String notOnlineFilePath;

    @ApiModelProperty(value = "关机包路径")
    private String shutdownFilePath;

    @ApiModelProperty(value = "疑似关机包路径")
    private String likeShutdownFilePath;

    @ApiModelProperty(value = "停机包路径")
    private String tingjiFilePath;

    @ApiModelProperty(value = "携号转网包路径")
    private String mnpFilePath;

    @ApiModelProperty(value = "号码错误包路径")
    private String unknownFilePath;

    @ApiModelProperty(value = "未知包路径")
    private String exceptionFailCountFilePath;

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