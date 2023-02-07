package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <pre>
 * 前端页面产品列表 查询参数对象
 * </pre>
 *
 * @author rivers
 * @since 2020-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ProductQueryParam对象", description = "产品列表查询参数")
public class ProductQueryParam {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "产品线编号")
    private String productGroupId;

}
