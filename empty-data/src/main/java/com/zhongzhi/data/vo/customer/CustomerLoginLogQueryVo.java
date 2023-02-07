package com.zhongzhi.data.vo.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 客户登录日志 查询结果对象
 * </pre>
 *
 * @author rivers
 * @date 2021-02-19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "CustomerLoginLogQueryVo对象", description = "客户登录日志查询参数")
public class CustomerLoginLogQueryVo implements Serializable {

    private static final long serialVersionUID = 1909853L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "请求ID")
    private String requestId;

    @ApiModelProperty(value = "客户ID")
    private Long customerId;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "区域")
    private String area;

    @ApiModelProperty(value = "运营商")
    private String operator;

    @ApiModelProperty(value = "tokenMd5值")
    private String token;

    @ApiModelProperty(value = "1:登录，2：登出")
    private Integer type;

    @ApiModelProperty(value = "是否成功 true:成功/false:失败")
    private Boolean success;

    @ApiModelProperty(value = "响应码")
    private Integer code;

    @ApiModelProperty(value = "失败消息记录")
    private String exceptionMessage;

    @ApiModelProperty(value = "浏览器名称")
    private String userAgent;

    @ApiModelProperty(value = "浏览器名称")
    private String browserName;

    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    @ApiModelProperty(value = "浏览器引擎名称")
    private String engineName;

    @ApiModelProperty(value = "浏览器引擎版本")
    private String engineVersion;

    @ApiModelProperty(value = "系统名称")
    private String osName;

    @ApiModelProperty(value = "平台名称")
    private String platformName;

    @ApiModelProperty(value = "是否是手机,0:否,1:是")
    private Boolean mobile;

    @ApiModelProperty(value = "移动端设备名称")
    private String deviceName;

    @ApiModelProperty(value = "移动端设备型号")
    private String deviceModel;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}