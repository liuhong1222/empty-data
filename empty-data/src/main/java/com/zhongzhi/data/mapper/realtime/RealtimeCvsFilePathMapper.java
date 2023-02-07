package com.zhongzhi.data.mapper.realtime;

import com.zhongzhi.data.entity.realtime.RealtimeCvsFilePath;
import com.zhongzhi.data.param.RealtimeCheckQueryParam;
import com.zhongzhi.data.vo.RealtimeCheckQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RealtimeCvsFilePathMapper {

	int saveOne(RealtimeCvsFilePath realtimeCvsFilePath);
	
	RealtimeCvsFilePath findOne(@Param("customerId")Long customerId, @Param("realtimeId")Long realtimeId);

	/**
	 * 查询历史数据
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return List<RealtimeCheckQueryVo>
	 */
	List<RealtimeCheckQueryVo> findList(RealtimeCheckQueryParam realtimeCheckQueryParam);

	RealtimeCheckQueryVo findLast(Long id);

	/**
	 * 删除
	 * @date 2021/11/17
	 * @param id
	 * @return int
	 */
    int delete(Long id);
}
