package com.zhongzhi.data.mapper.international;

import com.zhongzhi.data.entity.international.InternationalCheckStatistics;
import com.zhongzhi.data.entity.international.InternationalCvsFilePath;
import com.zhongzhi.data.param.InternationalCheckQueryParam;
import com.zhongzhi.data.vo.InternationalCheckQueryVo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InternationalCvsFilePathMapper {

	int saveOne(InternationalCvsFilePath internationalCvsFilePath);
	
	InternationalCvsFilePath findOne(@Param("customerId")Long customerId, @Param("internationalId")Long internationalId);

    int delete(Long id);
    
    List<InternationalCheckQueryVo> pageList(InternationalCheckQueryParam param);
    
    InternationalCheckQueryVo findLastOne(@Param("customerId") Long customerId);
    
    List<InternationalCheckStatistics> statisticList(@Param("customerId") Long customerId,
			@Param("fromTime") String fromTime, @Param("endTime") String endTime);
}
