package com.zhongzhi.data.service.realtime;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.realtime.RealtimeDailyInfo;
import com.zhongzhi.data.mapper.realtime.RealtimeDailyInfoMapper;
import com.zhongzhi.data.param.DailyInfoParam;
import com.zhongzhi.data.util.ThreadLocalContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * 实时日统计实现类
 * @author liuh
 * @date 2022年11月24日
 */
@Slf4j
@Service
public class RealtimeDailyInfoService {

	@Autowired
	private RealtimeDailyInfoMapper realtimeDailyInfoMapper;
	
	public PageInfo<RealtimeDailyInfo> pageList(DailyInfoParam dailyInfoParam) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(dailyInfoParam.getPage(), dailyInfoParam.getSize());
		
		dailyInfoParam.setCustomerId(customerId.toString());
		List<RealtimeDailyInfo> list = realtimeDailyInfoMapper.findList(dailyInfoParam);
		return new PageInfo<>(list);
	}
}
