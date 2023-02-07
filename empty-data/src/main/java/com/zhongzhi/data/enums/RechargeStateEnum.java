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

package com.zhongzhi.data.enums;

/**
 * 充值类型状态枚举
 *
 * @author rivers
 * @since 2020-2-18
 **/
public enum RechargeStateEnum implements BaseEnum {
    COMPANY_TRANSFER(0, "对公转账"),
    ALIPAY_QRCODE(1, "支付宝扫码付"),
    REGISTER(2, "注册赠送"),
    GIVING(3, "赠送"),
    COMPANY_ALIPAY(4, "对公支付宝转账"),
    PERSONAL_ALIPAY(5, "对私支付宝"),
    WECHAT_PAY(6, "对私微信"),
    PERSONAL_TRANSFER(7, "对私转账"),
    WEIXIN_QRCODE(8,"微信扫码付");

    private Integer code;
    private String desc;

    RechargeStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
