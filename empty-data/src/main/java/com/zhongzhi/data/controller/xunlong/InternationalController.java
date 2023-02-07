package com.zhongzhi.data.controller.xunlong;

import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.international.InternationalCheckStatistics;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.InternationalCheckQueryParam;
import com.zhongzhi.data.service.international.InternationalService;
import com.zhongzhi.data.vo.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 国际号码检测
 * @author liuh
 * @date 2022年6月8日
 */
@Slf4j
@RestController
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RequestMapping("/front/international")
public class InternationalController {

	@Autowired
	private InternationalService internationalService;

	@RequestMapping("/checkByFile")
	@ApiOperation(value = "执行国际号码检测", notes = "执行国际号码检测")
    public ApiResult internationalCheckByFile(String fileId, String countryCode,HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return internationalService.internationalCheckByFile(Long.valueOf(fileId),countryCode);
	}
	
	@RequestMapping("/getTestProcessMobile")
	@ApiOperation(value = "国际检测进度查询", notes = "国际检测进度查询")
    public ApiResult getTestProcessMobile(String fileId, String sendID,HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}
		
	    return internationalService.getTestProcessMobile(Long.valueOf(fileId),sendID);
	}

	/**
	 * 国际检测记录分页列表
	 */
	@PostMapping("/getInternationalPageList")
	@ApiOperation(value = "获取国际检测分页列表", notes = "国际检测记录分页列表", response = InternationalCheckQueryVo.class)
	public ApiResult<PageInfo<InternationalCheckQueryVo>> getRealtimePageList(@Valid @RequestBody InternationalCheckQueryParam param) throws Exception {
	    return ApiResult.ok(internationalService.getInternationalPageList(param));
	}

	/**
	 * 删除国际检测记录
	 */
	@FrontAgent
	@PostMapping("/delete/{id}")
	@ApiOperation(value = "删除国际检测对象", notes = "删除国际检测记录", response = ApiResult.class)
	public ApiResult<Boolean> deleteInternationalCheck(@PathVariable("id") Long id) throws Exception {
		return internationalService.delete(id);
	}

	/**
	 * 获取最新的一条国际检测记录
	 */
	@GetMapping("/getLatestInternational")
	@ApiOperation(value = "获取最新的国际检测对象详情", notes = "查看最新的国际检测记录", response = InternationalCheckQueryVo.class)
	public ApiResult<InternationalCheckQueryVo> getLatestInternational() throws Exception {
		return internationalService.getLatestInternational();
	}

	/**
	 * 统计客户国际检测数据
	 * @param year
	 * @param month
	 */
	@GetMapping("/statistics/{year}/{month}")
	@ApiOperation(value = "统计客户国际检测数据", notes = "统计客户国际检测数据", response = InternationalCheckStatistics.class)
	public ApiResult<List<InternationalCheckStatistics>> statistics(@PathVariable("year") int year,
															   @PathVariable("month") int month) throws Exception {
		return ApiResult.ok(internationalService.statistics(year, month));
	}
}
