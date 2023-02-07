package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.empty.CvsFilePath;
import com.zhongzhi.data.param.EmptyCheckQueryParam;
import com.zhongzhi.data.vo.EmptyCheckQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CvsFilePathMapper {
	
	int saveOne(CvsFilePath cvsFilePath);
	
	CvsFilePath findOne(@Param("customerId")Long customerId,@Param("emptyId")Long emptyId);

	EmptyCheckQueryVo findLast(@Param("customerId")Long customerId);

	/**
	 * 查询历史记录
	 * @date 2021/11/15
	 * @param emptyQueryParam
	 * @return List<EmptyCheckQueryVo>
	 */
	List<EmptyCheckQueryVo> findList(EmptyCheckQueryParam emptyQueryParam);

	/**
	 * 删除
	 * @date 2021/11/17
	 * @param id
	 * @return int
	 */
    int delete(Long id);
}
