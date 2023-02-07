package com.zhongzhi.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class MailVo {

    @ApiModelProperty("邮件id")
    private String id;
    @ApiModelProperty("邮件发送人")
    private String from;
    @ApiModelProperty("邮件接收人,多个以逗号分隔")
    private String to;
    @ApiModelProperty("邮件主题")
    private String subject;
    @ApiModelProperty("邮件内容")
    private String text;
    @ApiModelProperty("发送时间")
    private Date sentDate;
    @ApiModelProperty("抄送,多个以逗号分隔")
    private String cc;
    @ApiModelProperty("密送,多个以逗号分隔")
    private String bcc;
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("报错信息")
    private String error;
    @ApiModelProperty("邮件附件")
    @JsonIgnore
    private MultipartFile[] multipartFiles;
}
