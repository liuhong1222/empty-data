package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.MyNotice;
import com.zhongzhi.data.param.MyNoticeQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author xybb
 * @date 2021-11-06
 */
@Mapper
public interface MyNoticeMapper {

    /**
     * 我的消息-列表
     * @date 2021/11/6
     * @param param
     * @return List<MyNotice>
     */
    List<MyNotice> getMyNoticePageList(@Param("param") MyNoticeQueryParam param);

    /**
     * 我的消息-详情
     * @date 2021/11/6
     * @param id
     * @return MyNotice
     */
    MyNotice getMyNoticeById(Serializable id);

    /**
     * 我的消息-删除
     * @date 2021/11/6
     * @param id
     * @return int
     */
    int delete(Long id);

    /**
     * 我的消息-设为已读
     * @date 2021/11/6
     * @param myNotice
     * @return int
     */
    int update(MyNotice myNotice);
}
