package com.zhongzhi.data.service.direct;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongzhi.data.entity.direct.IntDirectCheck;
import com.zhongzhi.data.mapper.direct.IntDirectCheckMapper;
import com.zhongzhi.data.util.ThreadLocalContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 定向国际检测记录实现类
 * @author liuh
 * @date 2022年10月18日
 */
@Slf4j
@Service
public class IntDirectCheckService {

	@Autowired
	private IntDirectCheckMapper inteDirectCheckMapper;
	
	public IntDirectCheck findOne(Long customerId,Long intDirectId) {
		return inteDirectCheckMapper.findOne(customerId, intDirectId);
	}
	
	public int updateOne(IntDirectCheck intDirectCheck) {
		return inteDirectCheckMapper.updateOne(intDirectCheck);
	}
	
	public int saveOne(IntDirectCheck intDirectCheck) {
		return inteDirectCheckMapper.saveOne(intDirectCheck);
	}
	
	public int saveList(List<IntDirectCheck> list) {
		return inteDirectCheckMapper.saveList(list);
	}

	public List<IntDirectCheck> findByStatusAndCreateTime(int status, Date date) {
		return inteDirectCheckMapper.findByStatusAndCreateTime(status, date);
	}

	public PageInfo<IntDirectCheck> getTestRecord(int page, int size) {
		Long customerId = ThreadLocalContainer.getCustomerId();
		PageHelper.startPage(page, size);
		List<IntDirectCheck> list = inteDirectCheckMapper.getTestRecord(page, size, customerId);
		PageInfo<IntDirectCheck> info = new PageInfo<>(list);
		return info;
	}
}
