package com.zhongzhi.data.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类基类
 * @author xybb
 * @date 2021-03-08
 */
@Data
@NoArgsConstructor
public class BaseEntity implements Serializable {

	/**
	 * id
	 */
	@ApiModelProperty(value = "主键")
	private Long id;

    /**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

    /**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

}
