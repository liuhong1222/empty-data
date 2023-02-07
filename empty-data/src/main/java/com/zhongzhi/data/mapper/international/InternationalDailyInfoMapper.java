package com.zhongzhi.data.mapper.international;

import com.zhongzhi.data.entity.international.InternationalDailyInfo;
import com.zhongzhi.data.param.DailyInfoParam;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface InternationalDailyInfoMapper {

	List<InternationalDailyInfo> findList(DailyInfoParam dailyInfoParam);
}
