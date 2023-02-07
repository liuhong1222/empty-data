package com.zhongzhi.data.mapper.direct;

import com.zhongzhi.data.entity.direct.DirectDailyInfo;
import com.zhongzhi.data.param.DailyInfoParam;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DirectDailyInfoMapper {

	List<DirectDailyInfo> findList(DailyInfoParam dailyInfoParam);
}
