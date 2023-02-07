package com.zhongzhi.data.controller.xunlong;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.empty.EmptyCheck;
import com.zhongzhi.data.entity.empty.EmptyCheckStatistics;
import com.zhongzhi.data.entity.realtime.RealtimeCheck;
import com.zhongzhi.data.entity.realtime.RealtimeCheckStatistics;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.RealtimeCheckQueryParam;
import com.zhongzhi.data.service.realtime.RealtimeCheckService;
import com.zhongzhi.data.service.realtime.RealtimeService;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 实时检测
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@RestController
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RequestMapping("/front/realtime")
public class RealtimeController {

	@Autowired
	private RealtimeCheckService realtimeCheckService;
	
	@Autowired
	private RealtimeService realtimeService;

	@RequestMapping("/realtimeCheckByFile")
	@ApiOperation(value = "执行实时检测", notes = "执行实时检测")
    public ApiResult realtimeCheckByFile(String fileId, HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return realtimeService.realtimeCheckByFile(Long.valueOf(fileId));
	}
	
	@RequestMapping("/getTestProcessMobile")
	@ApiOperation(value = "实时检测进度查询", notes = "实时检测进度查询")
    public ApiResult getTestProcessMobile(String fileId, HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return realtimeService.getTestProcessMobile(Long.valueOf(fileId));
	}

	@RequestMapping("/mobileStatusStatic")
	@ApiOperation(value = "号码实时查询接口在线测试", notes = "号码实时查询接口在线测试")
    public ApiResult<RealtimeResult> mobileStatusStatic(String mobile, HttpServletRequest request) {
		if(StringUtils.isBlank(mobile)) {
			return ApiResult.fail("mobile不能为空");
		}

	    return realtimeService.mobileStatusStatic(mobile,false);
	}

	@RequestMapping("/mobileStatusStardard")
	@ApiOperation(value = "号码实时查询标准版接口在线测试", notes = "号码实时查询标准版接口在线测试")
    public ApiResult<RealtimeResult> mobileStatusStardard(String mobile, HttpServletRequest request) {
		if(StringUtils.isBlank(mobile)) {
			return ApiResult.fail("mobile不能为空");
		}

	    return realtimeService.mobileStatusStatic(mobile,true);
	}
	
	@RequestMapping("/realtimeCheckBySingle")
	@ApiOperation(value = "号码实时查询单个号码在线测试", notes = "号码实时查询单个号码接口在线测试")
    public ApiResult<String> realtimeCheckBySingle(String mobile, HttpServletRequest request) {
		if(StringUtils.isBlank(mobile)) {
			return ApiResult.fail("mobile不能为空");
		}

	    return realtimeService.realtimeCheckBySingle(mobile);
	}

	/**
	 * 实时检测记录分页列表
	 */
	@PostMapping("/getRealtimePageList")
	@ApiOperation(value = "获取实时检测分页列表", notes = "实时检测记录分页列表", response = RealtimeCheckQueryVo.class)
	public ApiResult<PageInfo<RealtimeCheckQueryVo>> getRealtimePageList(@Valid @RequestBody RealtimeCheckQueryParam realtimeCheckQueryParam) throws Exception {
	    PageInfo<RealtimeCheckQueryVo> info = realtimeService.getRealtimePageList(realtimeCheckQueryParam);
	    return ApiResult.ok(info);
	}

	/**
	 * 删除实时检测记录
	 */
	@FrontAgent
	@PostMapping("/delete/{id}")
	@ApiOperation(value = "删除实时检测对象", notes = "删除实时检测记录", response = ApiResult.class)
	public ApiResult<Boolean> deleteRealtimeCheck(@PathVariable("id") Long id) throws Exception {
		return realtimeService.delete(id);
	}

	/**
	 * 获取最新的一条实时检测记录
	 */
	@GetMapping("/getLatestRealtime")
	@ApiOperation(value = "获取最新的实时检测对象详情", notes = "查看最新的实时检测记录", response = RealtimeCheck.class)
	public ApiResult<RealtimeCheckQueryVo> getLatestRealtime() throws Exception {
		return realtimeService.getLatestRealtime();
	}

	/**
	 * 统计客户实时检测数据
	 * @date 2021/11/16
	 * @param year
	 * @param month
	 * @return ApiResult<List<EmptyCheckStatistics>>
	 */
	@GetMapping("/statistics/{year}/{month}")
	@ApiOperation(value = "统计客户实时检测数据", notes = "统计客户实时检测数据", response = EmptyCheckStatistics.class)
	public ApiResult<List<RealtimeCheckStatistics>> statistics(@PathVariable("year") int year,
															   @PathVariable("month") int month) throws Exception {
		List<RealtimeCheckStatistics> list = realtimeService.statistics(year, month);
		return ApiResult.ok(list);
	}

	/**
	 * 获取客户号码实时检测统计信息
	 */
	@FrontAgent
	@GetMapping("/getRealtimeStatisticalData")
	@ApiOperation(value = "获取客户号码实时检测统计信息", notes = "获取客户号码实时检测统计信息", response = RealtimeStatisticalDataVo.class)
	public ApiResult<RealtimeStatisticalDataVo> getRealtimeStatisticalData() throws Exception {
		Long customerId = ThreadLocalContainer.getCustomerId();

		// 获取今日客户号码检测总消耗数，活跃号码数
		PersonalStatisticalDataBo todayData = realtimeCheckService.getStatisticalData(customerId,
				DateUtil.beginOfDay(new Date()).toJdkDate(), DateUtil.endOfDay(new Date()).toJdkDate());

		// 获取昨天客户号码检测总消耗数，活跃号码数
		PersonalStatisticalDataBo yesterdayData = realtimeCheckService.getStatisticalData(customerId,
				DateUtil.beginOfDay(DateUtil.yesterday()).toJdkDate(), DateUtil.endOfDay(DateUtil.yesterday()).toJdkDate());

		RealtimeStatisticalDataVo realtimeStatisticalDataVo = new RealtimeStatisticalDataVo();
		realtimeStatisticalDataVo
				.setCustomerId(customerId)
				.setTodayConsumeTotal(todayData != null ? todayData.getConsumeTotal() : 0)
				.setYesterdayConsumeTotal(yesterdayData != null ? yesterdayData.getConsumeTotal() : 0);
		return ApiResult.ok(realtimeStatisticalDataVo);
	}

	/**
	 * 实时检测记录（在线测试）-列表
	 * @date 2021/11/11
	 * @param page
	 * @param size
	 * @return ApiResult<PageInfo<RealtimeCheck>>
	 */
	@GetMapping("/getTestRecord")
	@ApiOperation(value = "查看实时检测在线测试记录", notes = "查看实时检测在线测试记录", response = EmptyCheck.class)
	public ApiResult<PageInfo<RealtimeCheck>> getTestRecord(int page, int size) {
		PageInfo<RealtimeCheck> info = realtimeCheckService.getTestRecord(page, size);
		return ApiResult.ok(info);
	}
}
