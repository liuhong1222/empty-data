package com.zhongzhi.data.service.international;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.international.InternationalCheck;
import com.zhongzhi.data.mapper.international.InternationalCheckMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 国际号码检测记录实现类
 * @author liuh
 * @date 2022年6月8日
 */
@Slf4j
@Service
public class InternationalCheckService {

	@Autowired
	private InternationalCheckMapper internationalCheckMapper;
	
	public InternationalCheck findOne(Long customerId,Long emptyId) {
		return internationalCheckMapper.findOne(customerId, emptyId);
	}
	
	public int updateOne(InternationalCheck internationalCheck) {
		return internationalCheckMapper.updateOne(internationalCheck);
	}
	
	public int saveOne(InternationalCheck internationalCheck) {
		return internationalCheckMapper.saveOne(internationalCheck);
	}
	
	public int saveList(List<InternationalCheck> list) {
		return internationalCheckMapper.saveList(list);
	}

	public List<InternationalCheck> findByStatusAndCreateTime(int status, Date date) {
		return internationalCheckMapper.findByStatusAndCreateTime(status, date);
	}

	public PageInfo<InternationalCheck> getTestRecord(int page, int size) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(page, size);
		List<InternationalCheck> list = internationalCheckMapper.getTestRecord(page, size, customerId);
		PageInfo<InternationalCheck> info = new PageInfo<>(list);
		return info;
	}
}
