package com.zhongzhi.data.param;

import lombok.Data;

/**
 * 日统计列表查询通用参数实体类
 * @author liuh
 * @date 2022年11月24日
 */
@Data
public class DailyInfoParam extends PageParam{

	private String startDay;
	
	private String endDay;
	
	private String staticType;
	
	private String customerId;
	
	private String productType;
	
}
