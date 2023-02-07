package com.zhongzhi.data.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.RedisConstant;
import com.zhongzhi.data.entity.agent.AgentAccount;
import com.zhongzhi.data.entity.customer.CustomerBalance;
import com.zhongzhi.data.entity.customer.CustomerOrderRecord;
import com.zhongzhi.data.enums.AlipayTradeStatusEnum;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.agent.AgentAccountService;
import com.zhongzhi.data.service.customer.CustomerBalanceService;
import com.zhongzhi.data.service.customer.CustomerOrderRecordService;
import com.zhongzhi.data.service.customer.CustomerRechargeService;
import com.zhongzhi.data.util.WXPayUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付回调实现类
 * @author liuh
 * @date 2022年6月20日
 */
@Slf4j
@Service
public class NotifyService {

	@Autowired
    private CustomerOrderRecordService customerOrderRecordService;

    @Autowired
    private CustomerRechargeService customerRechargeService;

    @Autowired
    private CustomerBalanceService customerBalanceService;

    @Autowired
    private AgentAccountService agentAccountService;

    @Autowired
    private RedisClient redisClient;
    
    @Transactional
	public String alipayNotify(HttpServletRequest request) {
		try {
			// 获取支付宝的通知返回参数
	        String charset = request.getParameter("charset");
	        charset = charset == null ? "utf-8" : charset;
	        // 商户订单号
	        String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(charset), StandardCharsets.UTF_8);
	        // 订单金额
	        String totalAmount = new String(request.getParameter("total_amount").getBytes(charset), StandardCharsets.UTF_8);
	        // 支付宝交易号
	        String tradeNo = new String(request.getParameter("trade_no").getBytes(charset), StandardCharsets.UTF_8);
	        // 交易状态
	        String tradeStatus = new String(request.getParameter("trade_status").getBytes(charset), StandardCharsets.UTF_8);
	        // 买家支付宝用户号
	        String buyerId = new String(request.getParameter("buyer_id").getBytes(charset), StandardCharsets.UTF_8);
	        // 买家支付宝账号
	        String buyerLogonId = new String(request.getParameter("buyer_logon_id").getBytes(charset), StandardCharsets.UTF_8);
	        
	        // 查询订单记录
	        CustomerOrderRecord customerOrderRecord = customerOrderRecordService.findByOutTradeNo(outTradeNo);
	        if (customerOrderRecord == null) {
	            return null;
	        }
	        
	        if (!AlipayTradeStatusEnum.WAIT_BUYER_PAY.getCode().equals(customerOrderRecord.getTradeStatus())) {
	            log.info("支付宝异步通知-交易状态有误-无需处理数据。外部订单号：{}，交易状态：{}", outTradeNo, tradeStatus);
	            return "success";
	        }

	        // 更新支付记录表信息
	        Long customerId = customerOrderRecord.getCustomerId();
	        customerOrderRecord
	                .setBuyerLogonId(buyerLogonId)
	                .setBuyerUserId(buyerId)
	                .setGoodsPrice(totalAmount)
	                .setTradeNo(tradeNo)
	                .setTradeStatus(AlipayTradeStatusEnum.valueOf(tradeStatus).getCode())
	                .setUpdateTime(new Date());
	        int i = customerOrderRecordService.update(customerOrderRecord);
            if (i<=0) {
                log.error("充值-修改订单记录表-失败。客户id：{}, customerOrderRecord:{}", customerId, customerOrderRecord);
                throw new BusinessException("支付宝异步通知更新支付记录-数据库处理失败-修改订单记录失败");
            }
            log.info("充值-1.修改订单记录表-成功。客户id：{}, customerOrderRecord:{}", customerId, customerOrderRecord);

	        if (AlipayTradeStatusEnum.TRADE_SUCCESS.getDesc().equals(tradeStatus) ||
	                AlipayTradeStatusEnum.TRADE_FINISHED.getDesc().equals(tradeStatus)) {
	            // 支付宝交易成功，保存一条客户支付记录
	            boolean result = customerRechargeService.saveByAlipayOutTradeNo(outTradeNo);
                log.info("充值-2.修改客户充值记录表-成功。客户id：{}, 外部订单号:{}", customerId, outTradeNo);
                if (result) {
                    // 补充：更新代理商余额表和客户余额表
                    updateAgentAccount(customerOrderRecord);
                    updateCustomerBalance(customerOrderRecord);
                    // 补充：更新redis余额
                    updateRedisBalance(customerOrderRecord);
                } else {
                	log.error("支付宝异步通知-新增客户支付宝支付交易记录-数据库处理失败");
                    throw new BusinessException(ApiCode.DAO_EXCEPTION.getMsg());
                }
                
                log.info("充值-finished-支付宝回调处理数据完成，外部订单号：{}。", outTradeNo);
	        }
	        return "success";
		} catch (Exception e) {
			log.error("扫码付充值回调异常，info:",e);
			throw new BusinessException("fail");
		}
	}
	
    @Transactional
    public String weixinNotify(String paramStr) throws Exception {
    	try {
			// 获取微信的通知返回参数
	        Map<String, String> notifyMap = WXPayUtil.xmlToMap(paramStr);
	        if(!Constant.WEIXINPAY_SUCCESS_CODE.equals(notifyMap.get("return_code").toString())) {
	        	return weixinResponse(false);
	        }
	        
	        if(!Constant.WEIXINPAY_SUCCESS_CODE.equals(notifyMap.get("result_code").toString())) {
	        	return weixinResponse(false);
	        }
	        
	        // 商户订单号
	        String outTradeNo = notifyMap.get("out_trade_no").toString();
	        // 订单金额
	        String totalAmount = notifyMap.get("total_fee").toString();
	        // 微信交易号
	        String tradeNo = notifyMap.get("transaction_id").toString();
	        // 交易状态
	        String tradeStatus = notifyMap.get("result_code").toString();
	        // 买家用户号
	        String buyerId = notifyMap.get("bank_type").toString();
	        // 买家账号
	        String buyerLogonId = notifyMap.get("openid").toString();
	        
	        // 查询订单记录
	        CustomerOrderRecord customerOrderRecord = customerOrderRecordService.findByOutTradeNo(outTradeNo);
	        if (customerOrderRecord == null) {
	            return weixinResponse(false);
	        }
	        
	        if (!AlipayTradeStatusEnum.WAIT_BUYER_PAY.getCode().equals(customerOrderRecord.getTradeStatus())) {
	            log.info("微信异步通知-交易状态有误-无需处理数据。外部订单号：{}，交易状态：{}", outTradeNo, tradeStatus);
	            return weixinResponse(false);
	        }

	        // 更新支付记录表信息
	        Long customerId = customerOrderRecord.getCustomerId();
	        customerOrderRecord
	                .setBuyerLogonId(buyerLogonId)
	                .setBuyerUserId(buyerId)
	                .setGoodsPrice(new BigDecimal(totalAmount).divide(new BigDecimal(100)).toString())
	                .setTradeNo(tradeNo)
	                .setTradeStatus(AlipayTradeStatusEnum.getCode(tradeStatus))
	                .setUpdateTime(new Date());
	        int i = customerOrderRecordService.update(customerOrderRecord);
            if (i<=0) {
                log.error("充值-修改订单记录表-失败。客户id：{}, customerOrderRecord:{}", customerId, customerOrderRecord);
                return weixinResponse(false);
            }
            log.info("充值-1.修改订单记录表-成功。客户id：{}, customerOrderRecord:{}", customerId, customerOrderRecord);

            // 微信交易成功，保存一条客户支付记录
            boolean result = customerRechargeService.saveByAlipayOutTradeNo(outTradeNo);
            log.info("充值-2.修改客户充值记录表-成功。客户id：{}, 外部订单号:{}", customerId, outTradeNo);
            if (result) {
                // 补充：更新代理商余额表和客户余额表
                updateAgentAccount(customerOrderRecord);
                updateCustomerBalance(customerOrderRecord);
                // 补充：更新redis余额
                updateRedisBalance(customerOrderRecord);
            } else {
            	log.error("微信异步通知-新增客户微信支付交易记录-数据库处理失败");
            	return weixinResponse(false);
            }
            
            log.info("充值-finished-微信回调处理数据完成，外部订单号：{}。response:{}", outTradeNo,JSON.toJSONString(weixinResponse(true)));
	        return weixinResponse(true);
		} catch (Exception e) {
			log.error("扫码付充值回调异常，info:",e);
			throw new BusinessException("FAIL");
		}    	
    }
    
    private String weixinResponse(Boolean flag) throws Exception {
    	if(flag) {
    		return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
    	}else {
    		return "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[失败]]></return_msg>" + "</xml> ";
    	}    	
    }
    
	/**
     * 如果redis余额存在，就更新。
     * @date 2021/11/15
     * @param customerOrderRecord
     * @return void
     */
    private void updateRedisBalance(CustomerOrderRecord customerOrderRecord) {

        long count = Long.parseLong(customerOrderRecord.getGoodsSpecifications());
        Integer category = customerOrderRecord.getCategory();
        if (category.equals(0)) {
            // 空号检测
            String emptyKey = RedisConstant.EMPTY_BALANCE_KEY + customerOrderRecord.getCustomerId();
            String emptyStr = redisClient.get(emptyKey);
            if (!StringUtils.isBlank(emptyStr)) {
            	Long nowBalance = redisClient.incrBy(emptyKey, count);
                log.info("充值-5.redis余额-空号余额-修改成功。充值前余额：{}，充值后余额：{}", emptyStr, nowBalance);
            }
        } else if(category.equals(1)){
            // 实时检测
            String realtimeKey = RedisConstant.REALTIME_BALANCE_KEY + customerOrderRecord.getCustomerId();
            String realtimeStr = redisClient.get(realtimeKey);
            if (!StringUtils.isBlank(realtimeStr)) {
            	Long nowBalance = redisClient.incrBy(realtimeKey, count);
                log.info("充值-5.redis余额-实时检测余额-修改成功。充值前余额：{}，充值后余额：{}", realtimeStr, nowBalance);
            }
        }else if(category.equals(2)) {
        	// 国际检测
            String internationalKey = RedisConstant.INTERNATIONAL_BALANCE_KEY + customerOrderRecord.getCustomerId();
            String internationalStr = redisClient.get(internationalKey);
            if (!StringUtils.isBlank(internationalStr)) {
            	Long nowBalance = redisClient.incrBy(internationalKey, count);
                log.info("充值-5.redis余额-国际检测余额-修改成功。充值前余额：{}，充值后余额：{}", internationalStr, nowBalance);
            }
        }else if(category.equals(4)) {
        	// 定向通用检测
            String directCommonKey = RedisConstant.DIRECT_COMMON_BALANCE_KEY + customerOrderRecord.getCustomerId();
            String directCommonStr = redisClient.get(directCommonKey);
            if (!StringUtils.isBlank(directCommonStr)) {
                Long nowBalance = redisClient.incrBy(directCommonKey, count);
                log.info("充值-5.redis余额-定向通用检测余额-修改成功。充值前余额：{}，充值后余额：{}", directCommonStr, nowBalance);
            }
        }else if(category.equals(5)) {
        	// line定向检测
            String lineDirectKey = RedisConstant.LINE_DIRECT_BALANCE_KEY + customerOrderRecord.getCustomerId();
            String lineDirectStr = redisClient.get(lineDirectKey);
            if (!StringUtils.isBlank(lineDirectStr)) {
                Long nowBalance = redisClient.incrBy(lineDirectKey, count);
                log.info("充值-5.redis余额-line定向检测余额-修改成功。充值前余额：{}，充值后余额：{}", lineDirectStr, nowBalance);
            }
        }
    }

    /**
     * 更新代理商余额表
     * @date 2021/11/8
     * @param customerOrderRecord
     * @return void
     */
    private void updateAgentAccount(CustomerOrderRecord customerOrderRecord) {
        Long agentId = customerOrderRecord.getAgentId();
        AgentAccount agentAccount = agentAccountService.findByAgentId(agentId);
        agentAccount.setAgentId(agentId);

        long count = Long.parseLong(customerOrderRecord.getGoodsSpecifications());
        BigDecimal changeAmount = new BigDecimal(customerOrderRecord.getGoodsPrice());
        Integer category = customerOrderRecord.getCategory();
        if (category.equals(0)) {
            // 空号检测产品。empty_balance, cust_recharge_money, cust_recharge_num
            // empty_recharge_number = empty_balance + cust_recharge_num
            agentAccount.setEmptyBalance(agentAccount.getEmptyBalance()-count);
            agentAccount.setCustRechargeMoney(agentAccount.getCustRechargeMoney().add(changeAmount));
            agentAccount.setCustRechargeNum(agentAccount.getCustRechargeNum()+count);
        } else if(category.equals(1)){
            // 实时检测产品。realtime_balance, cust_realtime_recharge_money, cust_realtime_recharge_num
            // realtime_recharge_number = realtime_balance + cust_realtime_recharge_num
            agentAccount.setRealtimeBalance(agentAccount.getRealtimeBalance()-count);
            agentAccount.setCustRealtimeRechargeMoney(agentAccount.getCustRealtimeRechargeMoney().add(changeAmount));
            agentAccount.setCustRealtimeRechargeNum(agentAccount.getCustRealtimeRechargeNum()+count);
        }else if(category.equals(2)){
        	agentAccount.setInternationalBalance(agentAccount.getInternationalBalance()-count);
            agentAccount.setCustInternationalRechargeMoney(agentAccount.getCustInternationalRechargeMoney().add(changeAmount));
            agentAccount.setCustInternationalRechargeNum(agentAccount.getCustInternationalRechargeNum()+count);
        }else if(category.equals(4)){
        	agentAccount.setDirectCommonBalance(agentAccount.getDirectCommonBalance()-count);
            agentAccount.setCustDirectCommonRechargeMoney(agentAccount.getCustDirectCommonRechargeMoney().add(changeAmount));
            agentAccount.setCustDirectCommonRechargeNum(agentAccount.getCustDirectCommonRechargeNum()+count);
        }else if(category.equals(5)){
        	agentAccount.setLineDirectBalance(agentAccount.getLineDirectBalance()-count);
            agentAccount.setCustLineDirectRechargeMoney(agentAccount.getCustLineDirectRechargeMoney().add(changeAmount));
            agentAccount.setCustLineDirectRechargeNum(agentAccount.getCustLineDirectRechargeNum()+count);
        }

        int i = agentAccountService.update(agentAccount);
        if (i<=0) {
        	log.error("代理商id：{}，修改代理商账户记录失败。agentAccount:{}", agentAccount.getAgentId(), JSON.toJSONString(agentAccount));
            throw new BusinessException("支付宝异步通知更新代理商账户记录-数据库处理失败");
        }
        log.info("充值-3.修改代理商账户表-成功。代理商id：{}，agentAccount:{}", agentAccount.getAgentId(), JSON.toJSONString(agentAccount));
    }

    /**
     * 更新客户余额表
     * @date 2021/11/8
     * @param customerOrderRecord
     * @return void
     */
    private void updateCustomerBalance(CustomerOrderRecord customerOrderRecord) {
        Long customerId = customerOrderRecord.getCustomerId();
        CustomerBalance customerBalance = new CustomerBalance();
        customerBalance.setCustomerId(customerId);

        long count = Long.parseLong(customerOrderRecord.getGoodsSpecifications());
        BigDecimal addAmount = new BigDecimal(customerOrderRecord.getGoodsPrice());
        Integer category = customerOrderRecord.getCategory();
        if (category.equals(0)) {
            // 空号检测产品。条数和余额（empty_count, empty_recharge_num, empty_recharge_money）
            customerBalance.setEmptyCount(count);
            customerBalance.setEmptyRechargeNum(count);
            customerBalance.setEmptyRechargeMoney(addAmount);

        } else if(category.equals(1)){
            // 实时检测产品。条数和余额（realtime_count, realtime_recharge_num, realtime_recharge_money）
            customerBalance.setRealtimeCount(count);
            customerBalance.setRealtimeRechargeNum(count);
            customerBalance.setRealtimeRechargeMoney(addAmount);
        }else if(category.equals(2)){
        	customerBalance.setInternationalCount(count);
            customerBalance.setInternationalRechargeNum(count);
            customerBalance.setInternationalRechargeMoney(addAmount);
        }else if(category.equals(4)){
        	customerBalance.setDirectCommonCount(count);
            customerBalance.setDirectCommonRechargeNum(count);
            customerBalance.setDirectCommonRechargeMoney(addAmount);
        }else if(category.equals(5)){
        	customerBalance.setLineDirectCount(count);
            customerBalance.setLineDirectRechargeNum(count);
            customerBalance.setLineDirectRechargeMoney(addAmount);
        }

        int i = customerBalanceService.update(customerBalance);
        if (i<=0) {
        	log.error("客户id：{}，修改客户余额记录失败。customerBalance:{}", customerBalance.getCustomerId(), JSON.toJSONString(customerBalance));
            throw new BusinessException("支付宝异步通知更新客户余额记录-数据库处理失败");
        }
        log.info("充值-4.修改客户余额记录表-成功。客户id：{}，customerBalance:{}", customerBalance.getCustomerId(), JSON.toJSONString(customerBalance));
    }
}
