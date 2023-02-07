package com.zhongzhi.data.util;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zhongzhi.data.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付工具类
 */
@Slf4j
@Component
public class AlipayUtil {

    public static final String URL = "https://openapi.alipay.com/gateway.do";

    public static final String FORMAT = "json";
    public static final String CHARSET = "UTF-8";
    public static final String SIGN_TYPE = "RSA2";

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;


    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 支付宝当面付 交易预下单接口
     *
     * @param appId
     * @param appPrivateKey
     * @param alipayPublicKey
     * @param orderNo
     * @param amount
     * @param subject
     * @param agentId
     * @return 支付二维码链接
     * @throws AlipayApiException
     */
    public String getAlipayQrCodeString(String appId, String appPrivateKey, String alipayPublicKey, String orderNo,
                                        String amount, String subject, String agentId) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(URL, appId, appPrivateKey, FORMAT, CHARSET, alipayPublicKey, SIGN_TYPE);
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        JSONObject jsonObject = new JSONObject();
        //商户订单号
        jsonObject.put("out_trade_no", orderNo);
        jsonObject.put("total_amount", amount);
        jsonObject.put("subject", subject);
        jsonObject.put("store_id", "agent" + agentId);
        //订单允许的最晚付款时间
        jsonObject.put("timeout_express", "90m");
        JSONObject extendParams = new JSONObject();
        extendParams.put("sys_service_provider_id", "2088041882438138");
        jsonObject.put("extend_params", extendParams);

        request.setBizContent(jsonObject.toString());
        request.setNotifyUrl("http://" + domain + contextPath + "front/notify");
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (!response.getCode().equals("10000")) {
                log.error("调用支付宝生成二维码接口失败。入参：{}，msg:{}, response:{}", jsonObject.toString(), response.getSubMsg(), com.alibaba.fastjson.JSONObject.toJSONString(response));
                throw new BusinessException(response.getSubMsg());
            } else {
                log.info("调用支付宝生成二维码接口成功。入参：{}。", jsonObject.toString());
                if ("10000".equals(response.getCode())) {
                    return response.getQrCode();
                }
            }
        } catch (AlipayApiException e) {
            log.error("调用支付宝生成二维码接口出现异常。入参：{}", jsonObject.toString());
            throw new BusinessException(e.getMessage());
        }
        return null;
    }

    /**
     * 支付宝当面付交易查询接口
     * {"alipay_trade_query_response":{"code":"10000","msg":"Success","buyer_logon_id":"274***@qq.com","buyer_pay_amount":"0.00","buyer_user_id":"2088302616934024","invoice_amount":"0.00","out_trade_no":"20200229153713885346523","point_amount":"0.00","receipt_amount":"0.00","total_amount":"88.88","trade_no":"2020022922001434021425786415","trade_status":"WAIT_BUYER_PAY"},"sign":"UPeycdBDcHk/EGCC2J7WMnAi3uilbwCNsObW7xJduehOUHBH3N9tryCxqyC+Y/hXL8vi7WYqghBiTIl71sPWyUQz66lhIgZiGHsKDhz/btdSg80ZazkXWVNDmHUnXIeiWjCFnVZtozpcSU3JFrnBjCOmOKqX63nkBh2AUb7gFFulEwEAR75+vg7kBc7gdtUk1Isnxov/lIkf5wBIfSI5g45QMGL/9xP4dDXMDyy9nsIVQVueWMVm2UNJAWohdIbMAkaM94rjbyDJyMCNaz8W+VImN+B7pXtvYnD9GiBj4754tfQ8bXlaRpbJ22vPFiiQBkmLFElsxoWs4puTqG5BrQ=="}Disconnected from the target VM, address: '127.0.0.1:64624', transport: 'socket'
     *
     * @param appId
     * @param appPrivateKey
     * @param alipayPublicKey
     * @param orderNo
     * @throws AlipayApiException
     */
    public AlipayTradeQueryResponse queryAlipayRecord(String appId, String appPrivateKey, String alipayPublicKey, String orderNo) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(URL, appId, appPrivateKey, FORMAT, CHARSET, alipayPublicKey, SIGN_TYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        JSONObject jsonObject = new JSONObject();
        //商户订单号
        jsonObject.put("out_trade_no", orderNo);
        request.setBizContent(jsonObject.toString()); //设置业务参数
        AlipayTradeQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
        log.info("支付宝交易查询结果：{}", response.getBody());
        return response;
    }

}
