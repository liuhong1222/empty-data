package com.zhongzhi.data.entity.realtime;

import java.util.Date;

import lombok.Data;

/**
 * 实时日统计数据表实体类
 * @author liuh
 * @date 2022年11月24日
 */
@Data
public class RealtimeDailyInfo {

	private Integer id;
	
	private Long dayInt;
	
	private Long customerId;
	
	private Long realtimeTotal;
	
	private Long normalNumber;
	
	private Long realtimeEmptyNumber;
	
	private Long oncallNumber;
	
	private Long notOnlineNumber;
	
	private Long shutdownNumber;
	
	private Long likeShutdownNumber;
	
	private Long tingjiNumber;
	
	private Long mnpNumber;
	
	private Long moberrNumber;
	
	private Long unknownNumber;
	
	private Integer staticType;
	
	private Date createTime;
	
	private Date updateTime;
}
