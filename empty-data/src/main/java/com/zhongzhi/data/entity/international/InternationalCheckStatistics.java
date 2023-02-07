package com.zhongzhi.data.entity.international;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 国际检测统计图表
 * @author liuh
 * @date 2022年6月10日
 */
@Data
@NoArgsConstructor
public class InternationalCheckStatistics {

    @ApiModelProperty("已激活（条）")
    private long activeNumber;

    @ApiModelProperty("未激活（条）")
    private long noRegisterNumber;
    
    @ApiModelProperty(value = "未注册（条）")
    private Long unknownNumber;

    @ApiModelProperty("总条数（不含无效号码）；null表示未检测条数")
    private long totalNumber;

    @ApiModelProperty("天")
    private String day;

}
