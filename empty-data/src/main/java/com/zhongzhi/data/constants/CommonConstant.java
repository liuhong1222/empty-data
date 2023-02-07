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

package com.zhongzhi.data.constants;

/**
 * 公共常量
 *
 * @author geekidea
 * @date 2018-11-08
 */
public interface CommonConstant {

    /**
     * 默认页码为1
     */
    Long DEFAULT_PAGE_INDEX = 1L;

    /**
     * 默认页大小为10
     */
    Long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 分页总行数名称
     */
    String PAGE_TOTAL_NAME = "total";

    /**
     * 分页数据列表名称
     */
    String PAGE_RECORDS_NAME = "records";

    /**
     * 分页当前页码名称
     */
    String PAGE_INDEX_NAME = "pageIndex";

    /**
     * 分页当前页大小名称
     */
    String PAGE_SIZE_NAME = "pageSize";

    /**
     * 登录用户
     */
    String LOGIN_SYS_USER = "loginSysUser";

    /**
     * 登录token
     */
    String JWT_DEFAULT_TOKEN_NAME = "token";

    /**
     * JWT用户名
     */
    String JWT_USERNAME = "username";

    /**
     * JWT刷新新token响应状态码
     */
    int JWT_REFRESH_TOKEN_CODE = 460;

    /**
     * JWT刷新新token响应状态码，
     * Redis中不存在，但jwt未过期，不生成新的token，返回361状态码
     */
    int JWT_INVALID_TOKEN_CODE = 461;

    /**
     * JWT Token默认密钥
     */
    String JWT_DEFAULT_SECRET = "666666";

    /**
     * JWT 默认过期时间，3600L，单位秒
     */
    Long JWT_DEFAULT_EXPIRE_SECOND = 3600L;

    /**
     * 默认头像
     */
    String DEFAULT_HEAD_URL = "";

    /**
     * 管理员角色名称
     */
    String ADMIN_ROLE_NAME = "管理员";

    String ADMIN_LOGIN = "adminLogin";

    /**
     * 验证码token
     */
    String VERIFY_TOKEN = "verifyToken";

    /**
     * 图片
     */
    String IMAGE = "image";

    /**
     * JPEG
     */
    String JPEG = "JPEG";

    /**
     * base64前缀
     */
    String BASE64_PREFIX = "data:image/png;base64,";

    /**
     * ..
     */
    String SPOT_SPOT = "..";

    /**
     * ../
     */
    String SPOT_SPOT_BACKSLASH = "../";

    /**
     * SpringBootAdmin登录信息
     */
    String ADMIN_LOGIN_SESSION = "adminLoginSession";

    /**
     * 用户浏览器代理
     */
    String USER_AGENT = "User-Agent";

    /**
     * referer url
     */
    String REFERER = "Referer";

    /**
     * 本机地址IP
     */
    String LOCALHOST_IP = "127.0.0.1";
    /**
     * 本机地址名称
     */
    String LOCALHOST_IP_NAME = "本机地址";
    /**
     * 局域网IP
     */
    String LAN_IP = "192.168";
    /**
     * 局域网名称
     */
    String LAN_IP_NAME = "局域网";

    /**
     * 活跃号码文件名
     */
    String ACTIVE_FILE_NAME = "活跃号(实号).txt";

    /**
     * 静默号码文件名
     */
    String SILENT_FILE_NAME = "沉默号.txt";

    /**
     * 风险号码文件名
     */
    String RISK_FILE_NAME = "风险号.txt";

    /**
     * 空号码文件名
     */
    String EMPTY_FILE_NAME = "空号.txt";
    /**
     * 缓存分类活跃号码文件名
     */
    String CLASSIFY_ACTIVE_FILE_NAME = "classify_active.txt";

    /**
     * 缓存分类静默号码文件名
     */
    String CLASSIFY_SILENT_FILE_NAME = "classify_silent.txt";

    /**
     * 缓存分类风险号码文件名
     */
    String CLASSIFY_RISK_FILE_NAME = "classify_risk.txt";

    /**
     * 缓存分类空号码文件名
     */
    String CLASSIFY_EMPTY_FILE_NAME = "classify_empty.txt";

    /**
     * 通过接口下载得到的活跃号码文件名
     */
    String DOWNLOAD_ACTIVE_FILE_NAME = "download_active.txt";

    /**
     * 通过接口下载得到的静默号码文件名
     */
    String DOWNLOAD_SILENT_FILE_NAME = "download_silent.txt";

    /**
     * 通过接口下载得到的风险号码文件名
     */
    String DOWNLOAD_RISK_FILE_NAME = "download_risk.txt";

    /**
     * 通过接口下载得到的空号码文件名
     */
    String DOWNLOAD_EMPTY_FILE_NAME = "download_empty.txt";

    /**
     * 加入重复号码的活跃号码文件名
     */
    String MODIFY_ACTIVE_FILE_NAME = "modify_active.txt";

    /**
     * 加入重复号码的静默号码文件名
     */
    String MODIFY_SILENT_FILE_NAME = "modify_silent.txt";

    /**
     * 加入重复号码的风险号码文件名
     */
    String MODIFY_RISK_FILE_NAME = "modify_risk.txt";

    /**
     * 加入重复号码的空号码文件名
     */
    String MODIFY_EMPTY_FILE_NAME = "modify_empty.txt";

    /**
     * 未识别的手机号文件名
     */
    String UNKNOWN_FILE_NAME = "unknown.txt";

    /**
     * 未匹配的手机号去重后保存的文件名
     */
    String DISTINCT_FILE_NAME = "distinct.txt";

    /**
     * 非法号码文件名
     */
    String ILLEGAL_FILE_NAME = "illegal.txt";

    /**
     * 用户上传待分类的号码文件名
     */
    String SOURCE_FILE_NAME = "source.txt";

    /**
     * 移动、联通、电信运营商识别前缀
     */
    String[] MOBILE_PREFIX =
            new String[]{"134", "135", "136", "137", "138", "139", "147", "148", "150", "151", "152", "157", "158",
                    "159", "165", "172", "178", "182", "183", "184", "187", "188", "195", "198", "1703", "1705",
                    "1706"};
    String[] UNICOM_PREFIX =
            new String[]{"130", "131", "132", "145", "146", "155", "156", "166", "167", "171", "175", "176", "185",
                    "186", "196", "1704", "1707", "1708", "1709"};
    String[] TELCOM_PREFIX =
            new String[]{"133", "149", "153", "162", "173", "174", "177", "180", "181", "189", "191", "193", "199",
                    "1700", "1701", "1702"};
}
