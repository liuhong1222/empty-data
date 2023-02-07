package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.ProductGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xybb
 * @date 2021-11-11
 */
@Mapper
public interface ProductGroupMapper {

    /**
     * 产品线管理分页列表
     * @date 2021/11/11
     * @param productGroupTemp
     * @return List<ProductGroup>
     */
    List<ProductGroup> listByCondition(ProductGroup productGroupTemp);
}
