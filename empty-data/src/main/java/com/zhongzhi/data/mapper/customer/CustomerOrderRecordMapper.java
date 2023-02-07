package com.zhongzhi.data.mapper.customer;

import com.zhongzhi.data.entity.customer.CustomerOrderRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author xybb
 * @date 2021-11-08
 */
@Mapper
public interface CustomerOrderRecordMapper {

    /**
     * 获取支付宝扫码付订单状态
     * @date 2021/11/8
     * @param orderNo
     * @return ApiResult<String>
     */
     CustomerOrderRecord findByOutOrderId(String orderNo);

     /**
      * 客户支付宝支付交易记录-新增
      * @date 2021/11/8
      * @param customerOrderRecord
      * @return void
      */
    int save(CustomerOrderRecord customerOrderRecord);

    /**
     * 客户支付宝支付交易记录-修改
     * @date 2021/11/8
     * @param customerOrderRecord
     * @return int
     */
    int update(CustomerOrderRecord customerOrderRecord);

    /**
     * 客户支付宝支付交易记录-查找（通过支付宝交易号和创建时间）
     * @date 2021/11/8
     * @param status
     * @param date
     * @return List<CustomerOrderRecord>
     */
    List<CustomerOrderRecord> findByTradeStatusAndCreateTime(@Param("status") Integer status, @Param("date") Date date);
}
