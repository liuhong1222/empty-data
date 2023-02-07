package com.zhongzhi.data.mapper.direct;

import com.zhongzhi.data.entity.direct.IntDirectCheckStatistics;
import com.zhongzhi.data.entity.direct.IntDirectCvsFilePath;
import com.zhongzhi.data.param.IntDirectCheckQueryParam;
import com.zhongzhi.data.vo.IntDirectCheckQueryVo;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IntDirectCvsFilePathMapper {

	int saveOne(IntDirectCvsFilePath intDirectCvsFilePath);
	
	IntDirectCvsFilePath findOne(@Param("customerId")Long customerId, @Param("intDirectId")Long intDirectId);

    int delete(Long id);
    
    List<IntDirectCheckQueryVo> pageList(IntDirectCheckQueryParam param);
    
    IntDirectCheckQueryVo findLastOne(@Param("customerId") Long customerId);
    
    List<IntDirectCheckStatistics> statisticList(@Param("customerId") Long customerId,
			@Param("fromTime") String fromTime, @Param("endTime") String endTime);
}
