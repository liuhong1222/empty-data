package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xybb
 * @date 2021-11-07
 */
@Mapper
public interface GoodsMapper {

    /**
     * 通过条件查询套餐列表
     * @date 2021/11/7
     * @param goods
     * @return List<Goods>
     */
    List<Goods> listByCondition(@Param("goods") Goods goods, @Param("limit") int limit);

    /**
     * 通过条件查询套餐
     * @date 2021/11/8
     * @param goods
     * @return List<Goods>
     */
    Goods findByCondition(Goods goods);
}
