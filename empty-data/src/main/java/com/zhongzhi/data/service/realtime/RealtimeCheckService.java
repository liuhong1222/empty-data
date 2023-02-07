package com.zhongzhi.data.service.realtime;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.realtime.RealtimeCheck;
import com.zhongzhi.data.mapper.realtime.RealtimeCheckMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.PersonalStatisticalDataBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 实时检测记录实现类
 * @author liuh
 * @date 2021年11月2日
 */
@Slf4j
@Service
public class RealtimeCheckService {

	@Autowired
	private RealtimeCheckMapper realtimeCheckMapper;
	
	public RealtimeCheck findOne(Long customerId,Long emptyId) {
		return realtimeCheckMapper.findOne(customerId, emptyId);
	}
	
	public int updateOne(RealtimeCheck realtimeCheck) {
		return realtimeCheckMapper.updateOne(realtimeCheck);
	}
	
	public int saveOne(RealtimeCheck realtimeCheck) {
		return realtimeCheckMapper.saveOne(realtimeCheck);
	}
	
	public int saveList(List<RealtimeCheck> list) {
		return realtimeCheckMapper.saveList(list);
	}

	/**
	 * 实时检测记录-查找（通过状态和时间）
	 * @date 2021/11/8
	 * @param status
	 * @param date
	 * @return List<RealtimeCheck>
	 */
	public List<RealtimeCheck> findByStatusAndCreateTime(int status, Date date) {
		return realtimeCheckMapper.findByStatusAndCreateTime(status, date);
	}

	/**
	 * 实时检测记录（在线测试）-列表
	 * @date 2021/11/11
	 * @param
	 * @return PageInfo<EmptyCheck>
	 */
	public PageInfo<RealtimeCheck> getTestRecord(int page, int size) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(page, size);
		List<RealtimeCheck> list = realtimeCheckMapper.getTestRecord(page, size, customerId);
		PageInfo<RealtimeCheck> info = new PageInfo<>(list);
		return info;
	}

	@Transactional(rollbackFor = Exception.class)
	public PersonalStatisticalDataBo getStatisticalData(Long customerId, Date fromTime, Date endTime) {
		return realtimeCheckMapper.selectStatisticalData(customerId, fromTime, endTime);
	}
}
