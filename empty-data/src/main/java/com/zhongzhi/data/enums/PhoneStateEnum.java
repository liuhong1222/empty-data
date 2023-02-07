/*
 * Copyright 2019-2029 geekidea2(https://github.com/geekidea2)
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
 * 手机号码状态枚举
 *
 * @author geekidea2
 * @since 2019-10-24
 **/
public enum PhoneStateEnum implements BaseEnum {
    ACTIVE(0, "活跃号"),
    SILENT(1, "沉默号"),
    RISK(2, "风险号"),
    EMPTY(3, "空号"),
    SHUTDOWN(4, "停机"),
    NOT_EXIST(5, "没有此号码"),
    EXCEPTION_FAIL(-1, "检测失败");

    private Integer code;
    private String desc;

    PhoneStateEnum(Integer code, String desc) {
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
