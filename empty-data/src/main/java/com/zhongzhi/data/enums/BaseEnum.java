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
 * 枚举类型父接口
 *
 * @author geekidea
 * @since 2018-11-08
 */
public interface BaseEnum {

    /**
     * 获取枚举索引
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取描述
     *
     * @return
     */
    String getDesc();

}
