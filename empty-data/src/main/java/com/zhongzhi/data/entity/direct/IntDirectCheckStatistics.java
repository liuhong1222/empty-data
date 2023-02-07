package com.zhongzhi.data.entity.direct;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定向国际检测统计图表
 * @author liuh
 * @date 2022年10月18日
 */
@Data
@NoArgsConstructor
public class IntDirectCheckStatistics {

    @ApiModelProperty("已激活（条）")
    private long activeNumber;

    @ApiModelProperty("未激活（条）")
    private long noRegisterNumber;

    @ApiModelProperty("总条数（不含无效号码）；null表示未检测条数")
    private long totalNumber;

    @ApiModelProperty("天")
    private String day;

}
