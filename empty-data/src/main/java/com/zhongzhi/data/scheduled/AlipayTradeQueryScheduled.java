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

package com.zhongzhi.data.scheduled;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.CustomerOrderRecord;
import com.zhongzhi.data.enums.AlipayTradeStatusEnum;
import com.zhongzhi.data.service.customer.CustomerOrderRecordService;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.service.customer.CustomerRechargeService;
import com.zhongzhi.data.util.AlipayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付宝当面付交易任务调度
 *
 * @author geekidea2
 * @since 2019-10-29
 **/
@Slf4j
@Component
public class AlipayTradeQueryScheduled {

    @Autowired
    private CustomerOrderRecordService customerOrderRecordService;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private CustomerRechargeService customerRechargeService;

    @Autowired
    private AlipayUtil alipayUtil;

    /**
     * 每10分钟执行一次
     */
//    @Scheduled(cron = "0 0/20 * * * ? ")
    public void alipayTradeQuery() throws Exception {
        log.debug("alipayTradeQuery Scheduled start...");
        List<CustomerOrderRecord> list = customerOrderRecordService.getWaitingToPayList();
        if (list != null) {
            Map<Long, List<CustomerOrderRecord>> map = list.stream().collect(Collectors.groupingBy(CustomerOrderRecord::getAgentId));
            map.forEach((key, value) -> {
                AgentSettings agentSettings = agentSettingsService.findByAgentId(key);
                for (CustomerOrderRecord bean : value) {
                    try {
                        // 支付宝交易查询
                        AlipayTradeQueryResponse result = alipayUtil.queryAlipayRecord(
                                agentSettings.getAlipayAppid(),
                                agentSettings.getApplicationPrivateKey(),
                                agentSettings.getAlipayPublicKey(),
                                bean.getOutOrderId());
                        if (result.isSuccess()) {
                            if (AlipayTradeStatusEnum.WAIT_BUYER_PAY.getDesc().equals(result.getTradeStatus())) {
                                continue;
                            }
                            // 更新支付记录表信息
                            bean.setBuyerLogonId(result.getBuyerLogonId())
                                    .setBuyerUserId(result.getBuyerUserId())
                                    .setGoodsPrice(result.getTotalAmount())
                                    .setTradeNo(result.getTradeNo())
                                    .setTradeStatus(AlipayTradeStatusEnum.valueOf(result.getTradeStatus()).getCode())
                                    .setUpdateTime(new Date());
                            customerOrderRecordService.update(bean);

                            if (AlipayTradeStatusEnum.TRADE_SUCCESS.getDesc().equalsIgnoreCase(result.getTradeStatus()) ||
                                    AlipayTradeStatusEnum.TRADE_FINISHED.getDesc().equalsIgnoreCase(result.getTradeStatus())) {
                                // 支付宝交易成功，保存一条客户支付记录
                                boolean saveResult = customerRechargeService.saveByAlipayOutTradeNo(result.getOutTradeNo());
                                log.info("保存客户充值记录{}", saveResult);
                            }
                        } else {
                            // 更新支付记录表信息
                            bean.setTradeStatus(AlipayTradeStatusEnum.TRADE_CLOSED.getCode())
                                    .setUpdateTime(new Date());
                            customerOrderRecordService.update(bean);
                        }
                    } catch (AlipayApiException e) {
                        log.error("支付宝交易信息查询接口调用异常，信息是：{}", e.getMessage());
                    } catch (Exception e) {
                        log.error("支付宝定时器更新支付记录异常，外部订单号：" + bean.getOutOrderId(), e.getMessage());
                    }
                }
            });
        }

    }

}
