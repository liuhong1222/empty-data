package com.zhongzhi.data.controller.xunlong;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.param.MultipartFileParam;
import com.zhongzhi.data.param.UploadFileStatusParam;
import com.zhongzhi.data.service.front.ChunkService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件分片上传
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
@RestController
@RequestMapping("/front/chunk")
public class ChunkController {
	
	@Autowired
	private ChunkService chunkService;
	
	@RequestMapping("/chunkUpload")
    @ApiOperation("大文件分片上传")
    public ApiResult chunkUpload(MultipartFileParam param, HttpServletRequest request) {
		if(StringUtils.isBlank(param.getIdentifier())) {
			return ApiResult.fail("md5不能为空");
		}
		
		if(param.getFile() == null) {
			return ApiResult.fail("未选中文件");
		}
		
		if(param.getTotalChunks() == null) {
			return ApiResult.fail("分片总数量不能为空");
		}
		
		if(param.getChunkNumber() == null) {
			return ApiResult.fail("当前文件分片不能为空");
		}
		
		if(param.getChunkSize() == null) {
			return ApiResult.fail("当前分片大小不能为空");
		}
		
		if(StringUtils.isBlank(param.getFileRealName())) {
			return ApiResult.fail("文件名称不能为空");
		}
		
	    return chunkService.upload(param.getFile(), param.getIdentifier(), param.getFileRealName(), 
				param.getTotalChunks(), param.getChunkNumber(),param.getChunkSize());
	}
	
	@RequestMapping("/uploadStatus")
    @ApiOperation("文件分片上传状态")
    public ApiResult uploadStatus(UploadFileStatusParam param, HttpServletRequest request) {
		if(StringUtils.isBlank(param.getMd5())) {
			return ApiResult.fail("文件md5不能为空");
		}
		
		if(param.getChunks() == null) {
			return ApiResult.fail("文件分片总数不能为空");
		}
		
		if(StringUtils.isBlank(param.getFileName())) {
			return ApiResult.fail("文件名称不能为空");
		}
		
	    return chunkService.uploadStatus(param);
	}
}
