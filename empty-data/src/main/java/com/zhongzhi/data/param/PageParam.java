package com.zhongzhi.data.param;

import lombok.Data;

/**
 * 分页
 * @author liuh
 * @date 2021年3月23日
 */
@Data
public class PageParam {

    /**
     * 当前页码
     */
    private Integer page=1;

    /**
     * 每页记录数
     */
    private Integer size=10;

}