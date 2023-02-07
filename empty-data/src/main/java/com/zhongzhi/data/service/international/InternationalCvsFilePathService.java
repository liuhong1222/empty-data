package com.zhongzhi.data.service.international;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhongzhi.data.entity.international.InternationalCheckStatistics;
import com.zhongzhi.data.entity.international.InternationalCvsFilePath;
import com.zhongzhi.data.mapper.international.InternationalCvsFilePathMapper;
import com.zhongzhi.data.param.InternationalCheckQueryParam;
import com.zhongzhi.data.vo.InternationalCheckQueryVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 国际文件检测记录实现类
 * @author liuh
 * @date 2022年6月9日
 */
@Slf4j
@Service
public class InternationalCvsFilePathService {

	@Autowired
	private InternationalCvsFilePathMapper internationalCvsFilePathMapper;
	
	public int saveOne(InternationalCvsFilePath internationalCvsFilePath) {
		return internationalCvsFilePathMapper.saveOne(internationalCvsFilePath);
	}
	
	public int delete(Long id) {
		return internationalCvsFilePathMapper.delete(id);
	}
	
	public InternationalCvsFilePath findOne(Long customerId, Long internationalId) {
		return internationalCvsFilePathMapper.findOne(customerId, internationalId);
	}
	
	public List<InternationalCheckQueryVo> pageList(InternationalCheckQueryParam param){
		return internationalCvsFilePathMapper.pageList(param);
	}
	
	public InternationalCheckQueryVo findLastOne(Long customerId) {
		return internationalCvsFilePathMapper.findLastOne(customerId);
	}
	
	public List<InternationalCheckStatistics> statisticList(Long customerId,String fromTime,String endTime){
		return internationalCvsFilePathMapper.statisticList(customerId, fromTime, endTime);
	}
}
