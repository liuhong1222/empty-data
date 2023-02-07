package com.zhongzhi.data.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.MyNotice;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.mapper.MyNoticeMapper;
import com.zhongzhi.data.param.MyNoticeQueryParam;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * 我的消息实现类
 * @author xybb
 * @date 2021-11-06
 */
@Service
public class MyNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(MyNoticeService.class);

    @Autowired
    private MyNoticeMapper myNoticeMapper;

    /**
     * 我的消息-列表
     * @date 2021/11/6
     * @param param
     * @return PageInfo<MyNotice>
     */
    public PageInfo<MyNotice> getMyNoticePageList(MyNoticeQueryParam param) {
        Long customerId = ThreadLocalContainer.getCustomerId();
        param.setCustomerId(customerId);
        PageHelper.startPage(param.getPage(), param.getSize());
        List<MyNotice> list = myNoticeMapper.getMyNoticePageList(param);
        PageInfo<MyNotice> info = new PageInfo<>(list);
        return info;
    }

    /**
     * 我的消息-详情
     * @date 2021/11/6
     * @param id
     * @return MyNotice
     */
    public MyNotice getMyNoticeById(Serializable id) {
        return myNoticeMapper.getMyNoticeById(id);
    }

    /**
     * 我的消息-删除
     * @date 2021/11/6
     * @param id
     * @return boolean
     */
    public boolean deleteMyNotice(Long id) {
        int i = myNoticeMapper.delete(id);
        if (i<=0) {
            logger.error("客户id：{}，个人中心-我的消息-删除失败。id:{}", ThreadLocalContainer.getCustomerId(), id);
            return false;
        } else {
            logger.info("客户id：{}，个人中心-我的消息-删除成功。id:{}", ThreadLocalContainer.getCustomerId(), id);
            return true;
        }
    }

    /**
     * 我的消息-设置已读
     * @date 2021/11/6
     * @param id
     * @return ApiResult<Boolean>
     */
    public ApiResult<Boolean> setRead(Long id) {
        MyNotice myNotice = new MyNotice();
        myNotice.setId(id);
        myNotice.setHaveRead(1);
        int i = myNoticeMapper.update(myNotice);
        if (i<=0) {
            logger.error("客户id：{}，个人中心-我的消息-设为已读失败。id:{}", ThreadLocalContainer.getCustomerId(), id);
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            logger.info("客户id：{}，个人中心-我的消息-设为已读成功。id:{}", ThreadLocalContainer.getCustomerId(), id);
            return ApiResult.ok(true);
        }
    }
}
