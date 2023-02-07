package com.zhongzhi.data.controller.xunlong;

import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.empty.EmptyCheck;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.param.EmptyCheckQueryParam;
import com.zhongzhi.data.service.empty.EmptyService;
import com.zhongzhi.data.vo.EmptyCheckQueryVo;
import com.zhongzhi.data.entity.empty.EmptyCheckStatistics;
import com.zhongzhi.data.vo.UnnMobileNewStatus;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 空号检测
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RestController
@RequestMapping("/front/empty")
public class EmptyController {
	
	@Autowired
	private EmptyService emptyService;

	@RequestMapping("/emptyCheckByFile")
	@ApiOperation(value = "执行空号检测", notes = "执行空号检测")
    public ApiResult emptyCheckByFile(String fileId, HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return emptyService.emptyCheckByFile(Long.valueOf(fileId));
	}
	
	@RequestMapping("/getTestProcessMobile")
	@ApiOperation(value = "空号检测进度查询", notes = "空号检测进度查询")
    public ApiResult getTestProcessMobile(String fileId, HttpServletRequest request) {
		if(StringUtils.isBlank(fileId)) {
			return ApiResult.fail("文件id不能为空");
		}

	    return emptyService.getTestProcessMobile(Long.valueOf(fileId));
	}

	@RequestMapping("/batchCheckNew")
	@ApiOperation(value = "空号检测api在线测试", notes = "空号检测api在线测试")
	public ApiResult<List<UnnMobileNewStatus>> batchCheckNew(String mobiles, HttpServletRequest request) {
		if(StringUtils.isBlank(mobiles)) {
			return ApiResult.fail("mobiles不能为空");
		}

		return emptyService.batchCheckNew(mobiles);
	}

	/**
	 * 空号检测记录分页列表
	 */
	@PostMapping("/getPageList")
	@ApiOperation(value = "获取Empty分页列表", notes = "空号检测记录分页列表", response = EmptyCheckQueryVo.class)
	public ApiResult<PageInfo<EmptyCheckQueryVo>> getEmptyPageList(@Valid @RequestBody EmptyCheckQueryParam emptyQueryParam) throws Exception {
		PageInfo<EmptyCheckQueryVo> info = emptyService.getEmptyPageList(emptyQueryParam);
		return ApiResult.ok(info);
	}

	/**
	 * 获取最近正在运行的记录
	 */
	@PostMapping("/getRunningList")
	@ApiOperation(value = "获取Empty分页列表", notes = "空号检测记录分页列表", response = EmptyCheckQueryVo.class)
	public ApiResult<List<EmptyCheckQueryVo>> getRunningList() throws Exception {
		List<EmptyCheckQueryVo> info = emptyService.getRunningList();
		return ApiResult.ok(info);
	}

    /**
     * 获取最新的一条空号检测记录
     */
    @GetMapping("/getLatestEmpty")
    @ApiOperation(value = "获取最新的Empty对象详情", notes = "查看最新的空号检测记录", response = EmptyCheck.class)
    public ApiResult<EmptyCheckQueryVo> getLatestEmpty() throws Exception {
        return emptyService.getLatestEmpty();
    }

	@GetMapping("/statistics/{year}/{month}")
	@ApiOperation(value = "统计客户空号检测数据", notes = "统计客户空号检测数据", response = EmptyCheckStatistics.class)
	public ApiResult<List<EmptyCheckStatistics>> statistics(@PathVariable("year") int year,
															@PathVariable("month") int month) throws Exception {
    	List<EmptyCheckStatistics> list = emptyService.statistics(year, month);
    	return ApiResult.ok(list);
	}

	/**
     * 删除空号检测记录
     */
    @FrontAgent
    @PostMapping("/delete/{id}/{isOldData}")
    @ApiOperation(value = "删除Empty对象", notes = "删除空号检测记录", response = ApiResult.class)
    public ApiResult<Boolean> deleteEmpty(@PathVariable("id") Long id, @PathVariable("isOldData") Integer isOldData) {
        return emptyService.delete(id, isOldData);
	}

	/**
	 * 空号检测记录（在线测试）-列表
	 * @date 2021/11/11
	 * @param
	 * @return ApiResult<PageInfo<EmptyCheck>>
	 */
	@GetMapping("/getTestRecord")
	@ApiOperation(value = "查看空号在线测试记录", notes = "查看空号在线测试记录", response = EmptyCheck.class)
	public ApiResult<PageInfo<EmptyCheck>> getTestRecord(int page, int size) {
	    PageInfo<EmptyCheck> info = emptyService.getTestRecord(page, size);
    	return ApiResult.ok(info);
	}
}
