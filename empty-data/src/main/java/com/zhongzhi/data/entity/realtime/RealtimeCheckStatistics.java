package com.zhongzhi.data.entity.realtime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实时检测统计
 * @author xybb
 * @date 2021-11-16
 */
@Data
@NoArgsConstructor
public class RealtimeCheckStatistics {

    @ApiModelProperty("实号（条）")
    private long normal;

    @ApiModelProperty("空号（条）")
    private long empty;

    @ApiModelProperty("通话中（条）")
    private long onCall;

    @ApiModelProperty("在网但不可用（条）")
    private long onlineButNotAvailable;

    @ApiModelProperty("关机（条）")
    private long shutdown;

    @ApiModelProperty("呼叫转移（条）")
    private long callTransfer;

    @ApiModelProperty("疑似关机（条）")
    private long suspectedShutdown;

    @ApiModelProperty("停机（条）")
    private long serviceSuspended;

    @ApiModelProperty("携号转网（条）")
    private long numberPortability;

    @ApiModelProperty("号码错误或未知（条）")
    private long unknown;

    @ApiModelProperty("检测文件中无效号码（条）")
    private long illegalNumber;

    @ApiModelProperty("总条数（不含无效号码）；null表示未检测条数")
    private long totalNumber;

    @ApiModelProperty("天")
    private String day;

}
