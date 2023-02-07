package com.zhongzhi.data.entity.international;

import java.util.Date;

import lombok.Data;

/**
 * 国际日统计数据表实体类
 * @author liuh
 * @date 2022年11月24日
 */
@Data
public class InternationalDailyInfo {

	private Integer id;
	
	private Long dayInt;
	
	private Long customerId;
	
	private Long internationalTotal;
	
	private Long activeNumber;
	
	private Long noRegisterNumber;
	
	private Long interUnknownNumber;
		
	private Integer staticType;
	
	private Date createTime;
	
	private Date updateTime;
}
