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

package com.zhongzhi.data.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 手机短信验证码参数
 *
 * @author rivers
 * @since 2020-02-15
 **/
@Data
@ApiModel("手机短信验证码参数")
public class SmsCodeParam {

    @NotBlank(message = "请输入手机号码")
    @ApiModelProperty("手机号码")
    private String phone;
   
    @ApiModelProperty("图形验证码randStr")
    private String randStr;

    @ApiModelProperty("图形验证码ticket")
    private String ticket;

    private String ip;
}
