package com.zhongzhi.data.service.direct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.direct.DirectDailyInfo;
import com.zhongzhi.data.mapper.direct.DirectDailyInfoMapper;
import com.zhongzhi.data.param.DailyInfoParam;
import com.zhongzhi.data.util.ThreadLocalContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * 定向日统计实现类
 * @author liuh
 * @date 2022年11月24日
 */
@Slf4j
@Service
public class DirectDailyInfoService {

	@Autowired
	private DirectDailyInfoMapper directDailyInfoMapper;
	
	public PageInfo<DirectDailyInfo> pageList(DailyInfoParam dailyInfoParam) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(dailyInfoParam.getPage(), dailyInfoParam.getSize());
		
		dailyInfoParam.setCustomerId(customerId.toString());
		List<DirectDailyInfo> list = directDailyInfoMapper.findList(dailyInfoParam);
		return new PageInfo<>(list);
	}
}
