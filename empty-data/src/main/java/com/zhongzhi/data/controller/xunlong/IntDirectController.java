package com.zhongzhi.data.controller.xunlong;

import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.international.InternationalCheckStatistics;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.IntDirectCheckQueryParam;
import com.zhongzhi.data.service.direct.DirectInternationalService;
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
 * 定向国际检测
 * @author liuh
 * @date 2022年10月18日
 */
@Slf4j
@RestController
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RequestMapping("/front/intDirect")
public class IntDirectController {

	@Autowired
	private DirectInternationalService directInternationalService;
	
	@RequestMapping("/checkByFile")
	@ApiOperation(value = "执行定向国际号码检测", notes = "执行定向国际号码检测")
    public ApiResult realtimeCheckByFile(String fileId, String countryCode,String productType,HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return directInternationalService.intDirectCheckByFile(Long.valueOf(fileId),countryCode,productType);
	}
	
	@RequestMapping("/getTestProcessMobile")
	@ApiOperation(value = "定向国际检测进度查询", notes = "定向国际检测进度查询")
    public ApiResult getTestProcessMobile(String fileId, String sendID,HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return directInternationalService.getTestProcessMobile(Long.valueOf(fileId),sendID);
	}

	/**
	 *定向 国际检测记录分页列表
	 */
	@PostMapping("/getIntDirectPageList")
	@ApiOperation(value = "获取国际检测分页列表", notes = "国际检测记录分页列表", response = InternationalCheckQueryVo.class)
	public ApiResult<PageInfo<IntDirectCheckQueryVo>> getRealtimePageList(@Valid @RequestBody IntDirectCheckQueryParam param) throws Exception {
	    return ApiResult.ok(directInternationalService.getIntDirectPageList(param));
	}

	/**
	 * 删除定向国际检测记录
	 */
	@FrontAgent
	@PostMapping("/delete/{id}")
	@ApiOperation(value = "删除国际检测对象", notes = "删除国际检测记录", response = ApiResult.class)
	public ApiResult<Boolean> deleteInternationalCheck(@PathVariable("id") Long id) throws Exception {
		return directInternationalService.delete(id);
	}

	/**
	 * 获取最新的一条定向国际检测记录
	 */
	@GetMapping("/getLatestIntDirect")
	@ApiOperation(value = "获取最新的国际检测对象详情", notes = "查看最新的国际检测记录", response = InternationalCheckQueryVo.class)
	public ApiResult<IntDirectCheckQueryVo> getLatestInternational() throws Exception {
		return directInternationalService.getLatestIntDirect();
	}

	/**
	 * 统计客户定向国际检测数据
	 * @param year
	 * @param month
	 */
	@GetMapping("/statistics/{year}/{month}")
	@ApiOperation(value = "统计客户国际检测数据", notes = "统计客户国际检测数据", response = InternationalCheckStatistics.class)
	public ApiResult<List<InternationalCheckStatistics>> statistics(@PathVariable("year") int year,
															   @PathVariable("month") int month) throws Exception {
		return ApiResult.ok(directInternationalService.statistics(year, month));
	}
}
