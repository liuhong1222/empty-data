package com.zhongzhi.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhongzhi.data.entity.Country;
import com.zhongzhi.data.mapper.CountryMapper;

/**
 * 国家编码实现类
 * @author liuh
 * @date 2022年6月10日
 */
@Service
public class CountryService {

	@Autowired
	private CountryMapper countryMapper;
	
	public List<Country> findList(){
		return countryMapper.findList();
	}
}
