/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhongzhi.data.vo.login;

import com.zhongzhi.data.vo.customer.CustomerExtQueryVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 登录用户对象，响应给前端
 * </p>
 *
 * @author geekidea
 * @since 2019-05-15
 **/
@Data
@Accessors(chain = true)
public class LoginCustomerVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "代理商编号")
    private Long agentId;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "联系邮箱")
    private String email;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "客户类型（1：企业，0：个人，9：其他）")
    private Integer customerType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "审批状态，0：待审核，9：已认证，1：已驳回")
    private Integer state;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * 充值总金额
     */
    @ApiModelProperty(value = "充值总计（元）")
    private String paymentAmountTotal;

    /**
     * 充值总条数
     */
    @ApiModelProperty(value = "充值总条数")
    private long rechargeNumberTotal;

    /**
     * 剩余总条数
     */
    @ApiModelProperty(value = "剩余条数")
    private long remainNumberTotal;

    @ApiModelProperty(value = "客户认证信息")
    private CustomerExtQueryVo customerExt;

}
