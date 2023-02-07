package com.zhongzhi.data.mapper.realtime;

import com.zhongzhi.data.entity.realtime.RealtimeCheck;
import com.zhongzhi.data.entity.realtime.RealtimeCheckStatistics;
import com.zhongzhi.data.param.RealtimeCheckQueryParam;
import com.zhongzhi.data.vo.PersonalStatisticalDataBo;
import com.zhongzhi.data.vo.RealtimeCheckQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RealtimeCheckMapper {

	int saveList(List<RealtimeCheck> list);
	
	int saveOne(RealtimeCheck realtimeCheck);
	
	int updateOne(RealtimeCheck realtimeCheck);
	
	RealtimeCheck findOne(@Param("customerId")Long customerId,@Param("emptyId")Long emptyId);

	/**
	 * 实时检测记录-查找（通过状态和时间）
	 * @date 2021/11/8
	 * @param status
	 * @param date
	 * @return List<RealtimeCheck>
	 */
    List<RealtimeCheck> findByStatusAndCreateTime(@Param("status") int status, @Param("date") Date date);

    /**
     * 实时检测记录（在线测试）-列表
     * @date 2021/11/11
     * @param page
	 * @param size
     * @return List<RealtimeCheck>
     */
    List<RealtimeCheck> getTestRecord(@Param("page") int page, @Param("size") int size, @Param("customerId") Long customerId);

	/**
	 * 查询指定时间范围内客户号码检测统计信息
	 *
	 * @param customerId 客户Id
	 * @param fromTime   开始时间
	 * @param endTime    终止时间
	 * @return
	 */
	PersonalStatisticalDataBo selectStatisticalData(@Param("customerId") Long customerId,
													@Param("fromTime") Date fromTime, @Param("endTime") Date endTime);

	/**
	 * 实时检测-删除
	 * @date 2021/11/15
	 * @param id
	 * @return ApiResult<Boolean>
	 */
	int delete(Long id);

	/**
	 * 实时检测记录分页列表
	 * @date 2021/11/15
	 * @param realtimeCheckQueryParam
	 * @return List<RealtimeCheckQueryVo>
	 */
	List<RealtimeCheckQueryVo> getRealtimePageList(RealtimeCheckQueryParam realtimeCheckQueryParam);

	/**
	 * 通过条件查询实时检测记录
	 * @date 2021/11/15
	 * @param realtimeCheckTemp
	 * @return RealtimeCheck
	 */
	RealtimeCheck findByCondition(RealtimeCheck realtimeCheckTemp);

	List<RealtimeCheckStatistics> statistics(Long customerId, String from, String end);
}
