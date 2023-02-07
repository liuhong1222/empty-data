package com.zhongzhi.data.service.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhongzhi.data.entity.sys.FileUpload;
import com.zhongzhi.data.mapper.FileUploadMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传表实现类
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@Service
public class FileUploadService {

	@Autowired
	private FileUploadMapper fileUploadMapper;
	
	public int saveOne(FileUpload fileUpload) {
		return fileUploadMapper.saveOne(fileUpload);
	}
	
	public FileUpload findOne(Long id) {
		return fileUploadMapper.findOne(id);
	}
}
