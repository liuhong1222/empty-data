package com.zhongzhi.data.entity.empty;

import java.util.Date;

import lombok.Data;

/**
 * 空号检测日统计表实体类
 * @author liuh
 * @date 2022年11月24日
 */
@Data
public class EmptyDailyInfo {

	private Integer id;
	
	private Long dayInt;
	
	private Long agentId;
	
	private Long customerId;
	
	private Long emptyTotal;
	
	private Long realNumber;
	
	private Long silentNumber;
	
	private Long emptyNumber;
	
	private Long riskNumber;
	
	private Integer staticType;
	
	private Date createTime;
	
	private Date updateTime;
}
