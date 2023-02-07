package com.zhongzhi.data.mapper;

import com.zhongzhi.data.entity.ApiSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author xybb
 * @date 2021-11-04
 */
@Mapper
public interface ApiSettingsMapper {

    /**
     * 对外api接口-新增
     * @date 2021/11/4
     * @param apiSettings
     * @return int
     */
    int save(ApiSettings apiSettings);

    /**
     * 对外api接口-查找（通过条件）
     * @date 2021/11/10
     * @param apiSettings
     * @return ApiSettings
     */
    ApiSettings findByCondition(ApiSettings apiSettings);

    ApiSettings findOne(@Param("customerId")Long customerId);
}
