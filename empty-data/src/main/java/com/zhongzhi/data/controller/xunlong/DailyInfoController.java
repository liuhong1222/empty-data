package com.zhongzhi.data.controller.xunlong;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.DailyInfoParam;
import com.zhongzhi.data.service.direct.DirectDailyInfoService;
import com.zhongzhi.data.service.empty.EmptyDailyInfoService;
import com.zhongzhi.data.service.international.InternationalDailyInfoService;
import com.zhongzhi.data.service.realtime.RealtimeDailyInfoService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 日统计数据
 * @author liuh
 * @date 2022年11月24日
 */
@Slf4j
@RestController
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RequestMapping("/front/dailyInfo")
public class DailyInfoController {

	@Autowired
	private EmptyDailyInfoService emptyDailyInfoService;
	
	@Autowired
	private RealtimeDailyInfoService realtimeDailyInfoService;
	
	@Autowired
	private InternationalDailyInfoService internationalDailyInfoService;
	
	@Autowired
	private DirectDailyInfoService directDailyInfoService;
	
	@RequestMapping("/list/empty")
	@ApiOperation(value = "空号检测日统计数据查询", notes = "空号检测日统计数据查询")
    public ApiResult emptyList(DailyInfoParam dailyInfoParam,HttpServletRequest request) {
	    return ApiResult.ok(emptyDailyInfoService.pageList(dailyInfoParam));
	}
	
	@RequestMapping("/list/realtime")
	@ApiOperation(value = "实时检测日统计数据查询", notes = "实时检测日统计数据查询")
    public ApiResult realtimeList(DailyInfoParam dailyInfoParam,HttpServletRequest request) {
	    return ApiResult.ok(realtimeDailyInfoService.pageList(dailyInfoParam));
	}
	
	@RequestMapping("/list/international")
	@ApiOperation(value = "国际检测日统计数据查询", notes = "国际检测日统计数据查询")
    public ApiResult internationalList(DailyInfoParam dailyInfoParam,HttpServletRequest request) {
	    return ApiResult.ok(internationalDailyInfoService.pageList(dailyInfoParam));
	}
	
	@RequestMapping("/list/direct")
	@ApiOperation(value = "定向检测日统计数据查询", notes = "定向检测日统计数据查询")
    public ApiResult directList(DailyInfoParam dailyInfoParam,HttpServletRequest request) {
	    return ApiResult.ok(directDailyInfoService.pageList(dailyInfoParam));
	}	
}
