package com.zhongzhi.data.service;

import com.zhongzhi.data.entity.empty.CvsFilePath;
import com.zhongzhi.data.mapper.CvsFilePathMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 空号在线检测结果包信息实现类
 * @author liuh
 * @date 2021年10月30日
 */
@Slf4j
@Service
public class CvsFilePathService {

	@Autowired
	private CvsFilePathMapper cvsFilePathMapper;
	
	public int saveOne(CvsFilePath cvsFilePath) {
		return cvsFilePathMapper.saveOne(cvsFilePath);
	}
	
	public CvsFilePath findOne(Long customerId,Long emptyId) {
		return cvsFilePathMapper.findOne(customerId,emptyId);
	}
}
