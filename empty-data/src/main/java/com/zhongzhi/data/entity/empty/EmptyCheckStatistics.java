package com.zhongzhi.data.entity.empty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 空号检测统计
 */
@Data
@ApiModel
public class EmptyCheckStatistics {
    @ApiModelProperty("实号包数量")
    private long realNumber;

    @ApiModelProperty("沉默包数量")
    private long silentNumber;

    @ApiModelProperty("风险包数量")
    private long riskNumber;

    @ApiModelProperty("空号包数量")
    private long emptyNumber;

    @ApiModelProperty("总数")
    private long totalNumber;

    @ApiModelProperty("天")
    private String day;

}
