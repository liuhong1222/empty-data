package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xybb
 * @date 2021-11-10
 */
@Mapper
public interface ProductMapper {

    /**
     * 产品-列表
     * @date 2021/11/10
     * @param product
     * @return List<Product>
     */
    List<Product> listByCondition(Product product);
}
