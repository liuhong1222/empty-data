package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.Country;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CountryMapper {

    List<Country> findList();

}
