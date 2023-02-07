package com.zhongzhi.data.mapper.realtime;

import com.zhongzhi.data.entity.realtime.RealtimeDailyInfo;
import com.zhongzhi.data.param.DailyInfoParam;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RealtimeDailyInfoMapper {

	List<RealtimeDailyInfo> findList(DailyInfoParam dailyInfoParam);
}
