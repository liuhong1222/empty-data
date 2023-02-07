package com.zhongzhi.data.mapper.empty;

import com.zhongzhi.data.entity.empty.EmptyDailyInfo;
import com.zhongzhi.data.param.DailyInfoParam;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmptyDailyInfoMapper {

	List<EmptyDailyInfo> findList(DailyInfoParam dailyInfoParam);
}
