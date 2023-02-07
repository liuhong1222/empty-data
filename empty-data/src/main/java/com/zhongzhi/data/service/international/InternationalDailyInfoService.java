package com.zhongzhi.data.service.international;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.international.InternationalDailyInfo;
import com.zhongzhi.data.mapper.international.InternationalDailyInfoMapper;
import com.zhongzhi.data.param.DailyInfoParam;
import com.zhongzhi.data.util.ThreadLocalContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * 国际日统计实现类
 * @author liuh
 * @date 2022年11月24日
 */
@Slf4j
@Service
public class InternationalDailyInfoService {

	@Autowired
	private InternationalDailyInfoMapper internationalDailyInfoMapper;
	
	public PageInfo<InternationalDailyInfo> pageList(DailyInfoParam dailyInfoParam) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(dailyInfoParam.getPage(), dailyInfoParam.getSize());
		
		dailyInfoParam.setCustomerId(customerId.toString());
		List<InternationalDailyInfo> list = internationalDailyInfoMapper.findList(dailyInfoParam);
		return new PageInfo<>(list);
	}
}
