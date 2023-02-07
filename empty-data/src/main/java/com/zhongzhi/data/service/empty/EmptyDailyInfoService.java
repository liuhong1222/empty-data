package com.zhongzhi.data.service.empty;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.empty.EmptyDailyInfo;
import com.zhongzhi.data.mapper.empty.EmptyDailyInfoMapper;
import com.zhongzhi.data.param.DailyInfoParam;
import com.zhongzhi.data.util.ThreadLocalContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * 空号日统计实现类
 * @author liuh
 * @date 2022年11月24日
 */
@Slf4j
@Service
public class EmptyDailyInfoService {

	@Autowired
	private EmptyDailyInfoMapper emptyDailyInfoMapper;
	
	public PageInfo<EmptyDailyInfo> pageList(DailyInfoParam dailyInfoParam) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(dailyInfoParam.getPage(), dailyInfoParam.getSize());
		
		dailyInfoParam.setCustomerId(customerId.toString());
		List<EmptyDailyInfo> list = emptyDailyInfoMapper.findList(dailyInfoParam);
		return new PageInfo<>(list);
	}
}
