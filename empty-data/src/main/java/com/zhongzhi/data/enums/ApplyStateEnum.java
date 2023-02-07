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
 * 审核状态枚举
 *
 * @author rivers
 * @since 2020-2-18
 **/
public enum ApplyStateEnum implements BaseEnum {
    INIT(0, "初始化"),
    CREATE(1, "发布待审核"),
    MODIFY(2, "修改待审核"),
    APPROVED(3, "已审核"),
    REJECTED(4, "已驳回"),
    DELETED(5, "已删除");

    private Integer code;
    private String desc;

    ApplyStateEnum(Integer code, String desc) {
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
