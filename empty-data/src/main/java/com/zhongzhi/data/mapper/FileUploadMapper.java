package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.sys.FileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author liuh
 * @date 2021年11月4日
 */
@Mapper
public interface FileUploadMapper {

    int saveOne(FileUpload fileUpload);
    
    FileUpload findOne(@Param("id")Long id);
}
