package com.zhongzhi.data.util;

import com.zhongzhi.data.api.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 步云短信接口工具类
 */
public class SmsUtil {

    public static final String smsUrl = "http://118.178.188.81/msg/HttpBatchSendSM";
    public static final String account = "zhangxm_khjc";
    public static final String pswd = "Zhongzhi123";
    private static final Logger log = LoggerFactory.getLogger(SmsUtil.class);

    public static ApiResult sendMsg(String mobile, String content) {
        // 调用发送短信接口
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("account", account);
        params.add("pswd", pswd);
        params.add("mobile", mobile);
        params.add("msg", content);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(smsUrl, requestEntity, String.class);
        if (responseEntity != null && responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            String[] result = responseEntity.getBody().split("\\n");
            if (result[0].endsWith(",0")) {
                return ApiResult.ok("短信发送成功");
            } else {
                log.error("短信接口调用失败，手机号：{}，内容：{}, 返回信息：{}", mobile, content, responseEntity.getBody());
                return ApiResult.fail("短信验证码发送失败");
            }
        } else {
            log.error("短信接口调用失败，手机号：{}，内容：{}, 返回信息：{}", mobile, content, responseEntity.getBody());
            return ApiResult.fail("短信验证码发送失败");
        }
    }
}
