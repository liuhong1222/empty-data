package com.zhongzhi.data.entity;

import java.util.Date;

import lombok.Data;

/**
 * 国际编码
 * @author liuh
 * @date 2022年6月10日
 */
@Data
public class Country {
	
	private int id;

	private String code;
	
	private String name;
	
	private String desc;
	
	private Date createTime;
}
