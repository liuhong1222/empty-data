package com.zhongzhi.data.mapper.international;

import com.zhongzhi.data.entity.international.InternationalCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface InternationalCheckMapper {

	int saveList(List<InternationalCheck> list);
	
	int saveOne(InternationalCheck internationalCheck);
	
	int updateOne(InternationalCheck internationalCheck);
	
	InternationalCheck findOne(@Param("customerId")Long customerId,@Param("internationalId")Long internationalId);

    List<InternationalCheck> findByStatusAndCreateTime(@Param("status") int status, @Param("date") Date date);

    List<InternationalCheck> getTestRecord(@Param("page") int page, @Param("size") int size, @Param("customerId") Long customerId);

	int delete(Long id);
}
