package com.zhongzhi.data.service.direct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhongzhi.data.entity.direct.IntDirectCheckStatistics;
import com.zhongzhi.data.entity.direct.IntDirectCvsFilePath;
import com.zhongzhi.data.mapper.direct.IntDirectCvsFilePathMapper;
import com.zhongzhi.data.param.IntDirectCheckQueryParam;
import com.zhongzhi.data.vo.IntDirectCheckQueryVo;
import lombok.extern.slf4j.Slf4j;

/**
 * 定向国际检测记录文件实现类
 * @author liuh
 * @date 2022年10月18日
 */
@Slf4j
@Service
public class IntDirectCvsFilePathService {

	@Autowired
	private IntDirectCvsFilePathMapper intDirectCvsFilePathMapper;
	
	public int saveOne(IntDirectCvsFilePath intDirectCvsFilePath) {
		return intDirectCvsFilePathMapper.saveOne(intDirectCvsFilePath);
	}
	
	public int delete(Long id) {
		return intDirectCvsFilePathMapper.delete(id);
	}
	
	public IntDirectCvsFilePath findOne(Long customerId, Long intDirectId) {
		return intDirectCvsFilePathMapper.findOne(customerId, intDirectId);
	}
	
	public List<IntDirectCheckQueryVo> pageList(IntDirectCheckQueryParam param){
		return intDirectCvsFilePathMapper.pageList(param);
	}
	
	public IntDirectCheckQueryVo findLastOne(Long customerId) {
		return intDirectCvsFilePathMapper.findLastOne(customerId);
	}
	
	public List<IntDirectCheckStatistics> statisticList(Long customerId,String fromTime,String endTime){
		return intDirectCvsFilePathMapper.statisticList(customerId, fromTime, endTime);
	}
}
