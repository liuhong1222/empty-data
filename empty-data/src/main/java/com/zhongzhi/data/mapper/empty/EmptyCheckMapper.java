package com.zhongzhi.data.mapper.empty;

import com.zhongzhi.data.entity.empty.EmptyCheck;
import com.zhongzhi.data.param.EmptyCheckQueryParam;
import com.zhongzhi.data.vo.EmptyCheckQueryVo;
import com.zhongzhi.data.entity.empty.EmptyCheckStatistics;
import com.zhongzhi.data.vo.PersonalStatisticalDataBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface EmptyCheckMapper {

	int saveList(List<EmptyCheck> list);
	
	int saveOne(EmptyCheck emptyCheck);
	
	int updateOne(EmptyCheck emptyCheck);
	
	EmptyCheck findOne(@Param("customerId")Long customerId,@Param("emptyId")Long emptyId);

	/**
	 * 获取分页对象
	 *
	 * @param emptyCheckQueryParam
	 * @return
	 */
	List<EmptyCheckQueryVo> getEmptyCheckPageList(@Param("param") EmptyCheckQueryParam emptyCheckQueryParam);

	/**
	 * 获取最近正在运行的记录
	 * @date 2021/11/9
	 * @param customerId
	 * @param fromTime
	 * @param endTime
	 * @return List<EmptyCheckQueryVo>
	 */
    List<EmptyCheckQueryVo> getRunningList(Long customerId, Date fromTime, Date endTime);

    /**
     * 通过条件查询空号检测记录
     * @date 2021/11/9
     * @param emptyCheckTemp
     * @return EmptyCheck
     */
	EmptyCheck findByCondition(EmptyCheck emptyCheckTemp);

	/**
	 * 统计空号检测数据
	 * @date 2021/11/9
	 * @param customerId 客户Id
	 * @param from 		 开始时间（包含
	 * @param end 		 结束时间(不包含）
	 * @return List<EmptyCheckStatistics> 按天统计数据
	 */
	List<EmptyCheckStatistics> statistics(Long customerId, String from, String end);

    PersonalStatisticalDataBo selectPersonalStatisticalData(Long customerId, Date fromTime, Date endTime);

    /**
     * 空号检测记录（在线测试）-列表
     * @date 2021/11/11
     * @param
     * @return PageInfo<EmptyCheck>
     */
	List<EmptyCheck> getTestRecord(int page, int size, Long customerId);

	/**
	 * 删除空号检测记录
	 * @date 2021/11/12
	 * @param id
	 * @return int
	 */
    int delete(Long id);
}
