package com.zhongzhi.data.entity.direct;

import java.util.Date;

import lombok.Data;

/**
 * 定向日统计表实体类
 * @author liuh
 * @date 2022年11月24日
 */
@Data
public class DirectDailyInfo {

	private Integer id;
	
	private Long dayInt;
	
	private Long agentId;
	
	private Long customerId;
	
	private String productType;
	
	private Long directTotal;
	
	private Long activeNumber;
	
	private Long noRegisterNumber;
			
	private Integer staticType;
	
	private Date createTime;
	
	private Date updateTime;
}
