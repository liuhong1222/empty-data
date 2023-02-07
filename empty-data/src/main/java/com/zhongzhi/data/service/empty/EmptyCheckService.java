package com.zhongzhi.data.service.empty;

import com.zhongzhi.data.entity.empty.EmptyCheck;
import com.zhongzhi.data.mapper.empty.EmptyCheckMapper;
import com.zhongzhi.data.vo.PersonalStatisticalDataBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 检测记录实现类
 * @author liuh
 * @date 2021年10月28日
 */
@Slf4j
@Service
public class EmptyCheckService {

	@Autowired
	private EmptyCheckMapper emptyCheckMapper;
	
	public int saveOne(EmptyCheck emptyCheck) {
		return emptyCheckMapper.saveOne(emptyCheck);
	}
	
	public int updateOne(EmptyCheck emptyCheck) {
		return emptyCheckMapper.updateOne(emptyCheck);
	}
	
	public EmptyCheck findOne(Long customerId,Long emptyId) {
		return emptyCheckMapper.findOne(customerId,emptyId);
	}
	
	public int saveList(List<EmptyCheck> list) {
		return emptyCheckMapper.saveList(list);
	}

	@Transactional(rollbackFor = Exception.class)
	public PersonalStatisticalDataBo getPersonalStatisticalData(Long customerId, Date fromTime, Date endTime) {
		return emptyCheckMapper.selectPersonalStatisticalData(customerId, fromTime, endTime);
	}
}
