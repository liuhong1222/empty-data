package com.zhongzhi.data.entity;

import eu.bitwalker.useragentutils.Version;
import lombok.Data;

/**
 * 请求设置信息
 * @author liuh
 * @date 2021年3月10日
 */
@Data
public class RequestDeviceInfo {
	
	/**
	 * 浏览器名称
	 */
	private String browserName;
	
	/**
	 * 浏览器版本
	 */
	private Version browserVersion;
	
	/**
	 * 操作系统名称
	 */
	private String operatingSystemName;

}
