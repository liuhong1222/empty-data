package com.zhongzhi.data.controller.xunlong;

import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.MyNotice;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.MyNoticeQueryParam;
import com.zhongzhi.data.service.MyNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 我的消息管理
 * @author xybb
 * @date 2021-11-05
 */
@RestController
@RequestMapping("/front/myNotice")
@Api("我的消息管理 API")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
public class MyNoticeController {

    @Autowired
    private MyNoticeService myNoticeService;

    /**
     * 我的消息-列表
     * @date 2021/11/6
     * @param myNoticeQueryParam 客户id，已读状态
     * @return ApiResult<PageInfo<MyNotice>>
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "获取MyNotice分页列表", notes = "我的消息管理分页列表", response = MyNotice.class)
    public ApiResult<PageInfo<MyNotice>> getMyNoticePageList(@Valid @RequestBody MyNoticeQueryParam myNoticeQueryParam) {
        PageInfo<MyNotice> info = myNoticeService.getMyNoticePageList(myNoticeQueryParam);
        return ApiResult.ok(info);
    }

    /**
     * 我的消息-详情
     * @date 2021/11/6
     * @param id
     * @return ApiResult<MyNotice>
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取MyNotice对象详情", notes = "查看我的消息管理", response = MyNotice.class)
    public ApiResult<MyNotice> getMyNotice(@PathVariable("id") Long id) {
        return ApiResult.ok(myNoticeService.getMyNoticeById(id));
    }

    /**
     * 我的消息-删除
     * @date 2021/11/6
     * @param id 消息id
     * @return ApiResult<Boolean>
     */
    @FrontAgent
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除MyNotice对象", notes = "删除我的消息管理", response = ApiResult.class)
    public ApiResult<Boolean> deleteMyNotice(@PathVariable("id") Long id) {
        boolean flag = myNoticeService.deleteMyNotice(id);
        return ApiResult.result(flag);
    }


    /**
     * 我的消息-设置已读
     * @date 2021/11/6
     * @param id
     * @return ApiResult<Boolean>
     */
    @FrontAgent
    @GetMapping("/setRead/{id}")
    @ApiOperation(value = "设置MyNotice已读", notes = "设置我的消息已读", response = Boolean.class)
    public ApiResult<Boolean> setRead(@PathVariable("id") Long id) {
        return myNoticeService.setRead(id);
    }

}
