package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.Faq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xybb
 * @date 2021-11-10
 */
@Mapper
public interface FaqMapper {

    /**
     * 产品常见问题-列表（通过条件）
     * @date 2021/11/10
     * @param faq
     * @return List<Faq>
     */
    List<Faq> listByCondition(Faq faq);

}
