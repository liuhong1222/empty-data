package com.zhongzhi.data.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.service.http.OkhttpService;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信支付
 * @author liuh
 * @date 2022年6月16日
 */
@Slf4j
@Service
public class WeixinPayService {
	
	private static final String tradeType = "NATIVE";
	
	@Autowired
	private OkhttpService okhttpService;
	
	public String createNativeOrder(BigDecimal amount,String orderNo,String goodsName,String ip) throws Exception {
		// 获取代理商设置缓存数据
		AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
		// 组装请求参数
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("appid", agentSettings.getWechatAppid());
		paramMap.put("mch_id", agentSettings.getWechatMchid());
		paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
		paramMap.put("body", goodsName);
		paramMap.put("out_trade_no", orderNo);
		paramMap.put("total_fee", amount.multiply(new BigDecimal(100)).toString());
		paramMap.put("spbill_create_ip", ip);
		paramMap.put("notify_url", agentSettings.getWechatpayNotify());
		paramMap.put("trade_type", tradeType);
		
		// 加签
		String paramXml = WXPayUtil.generateSignedXml(paramMap, agentSettings.getWechatKey());
		String resultXml = okhttpService.postXml(agentSettings.getWechatGateway(), paramXml);
		if(StringUtils.isBlank(resultXml)) {
			log.error("微信扫码付异常，接口返回为空");
			return null;
		}
		
		Map<String, String> map = WXPayUtil.xmlToMap(resultXml);
		if(!Constant.WEIXINPAY_SUCCESS_CODE.equals(map.get("return_code").toString())) {
			log.error("微信扫码付失败，info:{}",JSON.toJSONString(map));
			return null;
		}
		
		if(!Constant.WEIXINPAY_SUCCESS_CODE.equals(map.get("result_code").toString())) {
			log.error("微信扫码付失败，info:{}",JSON.toJSONString(map));
			return null;
		}
		
		log.info("微信扫码付扫码成功，info：{}",JSON.toJSONString(map));
		return map.get("code_url");
	}
}
