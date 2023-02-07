package com.zhongzhi.data.mapper.direct;

import com.zhongzhi.data.entity.direct.IntDirectCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface IntDirectCheckMapper {

	int saveList(List<IntDirectCheck> list);
	
	int saveOne(IntDirectCheck intDirectCheck);
	
	int updateOne(IntDirectCheck intDirectCheck);
	
	IntDirectCheck findOne(@Param("customerId")Long customerId,@Param("intDirectId")Long intDirectId);

    List<IntDirectCheck> findByStatusAndCreateTime(@Param("status") int status, @Param("date") Date date);

    List<IntDirectCheck> getTestRecord(@Param("page") int page, @Param("size") int size, @Param("customerId") Long customerId);

	int delete(Long id);
}
