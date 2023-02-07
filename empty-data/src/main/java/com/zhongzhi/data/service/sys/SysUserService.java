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

package com.zhongzhi.data.service.sys;

import cn.hutool.core.util.StrUtil;
import com.zhongzhi.data.entity.sys.SysUser;
import com.zhongzhi.data.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * 系统用户 服务实现类
 * </pre>
 *
 * @author geekidea
 * @since 2019-10-24
 */
@Service
public class SysUserService  {

    private static final Logger logger = LoggerFactory.getLogger(SysUserService.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    public String getEmailJoiningByAgentId(Long agentId) {
        List<SysUser> users = sysUserMapper.selectByAgentId(agentId);

        String emails = users.stream().filter(user -> StrUtil.isNotBlank(user.getEmail()))
                .map(user -> user.getEmail())
                .distinct()
                .collect(Collectors.joining(","));
        return emails;
    }
}
