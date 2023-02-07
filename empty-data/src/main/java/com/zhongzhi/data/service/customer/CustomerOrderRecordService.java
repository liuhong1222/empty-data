package com.zhongzhi.data.service.customer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;

import com.alipay.api.AlipayApiException;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.entity.Goods;
import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.CustomerOrderRecord;
import com.zhongzhi.data.enums.AlipayTradeStatusEnum;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.enums.RechargeStateEnum;
import com.zhongzhi.data.mapper.customer.CustomerOrderRecordMapper;
import com.zhongzhi.data.service.GoodsService;
import com.zhongzhi.data.service.WeixinPayService;
import com.zhongzhi.data.service.agent.AgentService;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.util.AlipayUtil;
import com.zhongzhi.data.util.Snowflake;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 客户支付宝支付交易记录实现类
 * @author xybb
 * @date 2021-11-08
 */
@Service
public class CustomerOrderRecordService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerOrderRecordService.class);

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private CustomerOrderRecordMapper customerOrderRecordMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentSettingsService agentSettingsService;

    @Autowired
    private AlipayUtil alipayUtil;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CustomerOrderRecordService customerOrderRecordService;
    
    @Autowired
    private WeixinPayService weixinPayService;

    /**
     * 获取支付宝扫码付订单状态
     * @date 2021/11/8
     * @param orderNo
     * @return ApiResult<String>
     */
    public ApiResult<String> getQrCodePayState(String orderNo) {
        CustomerOrderRecord customerOrderRecord = customerOrderRecordMapper.findByOutOrderId(orderNo);
        if (customerOrderRecord == null) {
            return ApiResult.fail("该订单不存在，请刷新页面重新下单");
        }
        return ApiResult.ok(customerOrderRecord.getTradeStatus());
    }

    /**
     * 获取支付宝支付二维码
     * @date 2021/11/8
     * @param id
     * @param amount
     * @return ApiResult<String>
     */
    public ApiResult<String> getAlipayQrCode(Long id, BigDecimal amount,Integer payType,String ip) throws Exception {
        // 1.获取套餐信息
        Goods goodsTemp = new Goods();
        goodsTemp.setId(id);
        Goods goods = goodsService.findByCondition(goodsTemp);

        // 2.获取充值条数
        String price = amount.toString();
        String specifications = goods.getSpecifications();
        if (goods.getType() == 1) {
            // 自定义充值计算充值条数
            if (StringUtils.isBlank(goods.getUnitPrice())) {
                return ApiResult.fail("自定义套餐未设置单价");
            }
            specifications = new BigDecimal(price).divide(new BigDecimal(goods.getUnitPrice()), RoundingMode.DOWN).toString();
        }

        // 3.校验代理商余额
        AgentSettings agentSettings = ThreadLocalContainer.getAgentSettings();
        long sumRemainsNumbers = 0;
        if (ProductTypeEnum.REALTIME.getCode().equals(goods.getCategory())) {
            sumRemainsNumbers = agentService.getAgentRealtimeBalance(agentSettings.getAgentId());
            logger.info("代理商：{}, 实时检测余额: {}", agentSettings.getAgentName(), sumRemainsNumbers);
        }else if (ProductTypeEnum.EMPTY.getCode().equals(goods.getCategory())){
            sumRemainsNumbers = agentService.getAgentEmptyBalance(agentSettings.getAgentId());
            logger.info("代理商：{}, 空号检测余额: {}", agentSettings.getAgentName(), sumRemainsNumbers);
        }else if (ProductTypeEnum.INTERNATIONAL.getCode().equals(goods.getCategory())){
        	sumRemainsNumbers = agentService.getAgentInternationalBalance(agentSettings.getAgentId());
            logger.info("代理商：{}, 国际检测余额: {}", agentSettings.getAgentName(), sumRemainsNumbers);
        }else if (ProductTypeEnum.DIRECT_COMMON.getCode().equals(goods.getCategory())){
        	sumRemainsNumbers = agentService.getAgentDirectCommonBalance(agentSettings.getAgentId());
            logger.info("代理商：{}, 定向通用检测余额: {}", agentSettings.getAgentName(), sumRemainsNumbers);
        }else {
        	sumRemainsNumbers = agentService.getAgentLineDirectBalance(agentSettings.getAgentId());
            logger.info("代理商：{}, line定向检测余额: {}", agentSettings.getAgentName(), sumRemainsNumbers);
        }
        
        if (BigDecimal.valueOf(sumRemainsNumbers).compareTo(new BigDecimal(specifications)) < 0) {
            logger.info("代理商：{}，余额不足。", agentSettings.getAgentName());
            return ApiResult.fail("获取支付二维码失败，请联系客服人员");
        }

        // 4.校验套餐充值金额
        if (goods.getType() == 1) {
            // 自定义充值
            if (goods.getMinPayAmount() == null || new BigDecimal(goods.getMinPayAmount()).compareTo(new BigDecimal(amount.toString())) > 0) {
                return ApiResult.fail("充值金额不能少于" + goods.getMinPayAmount() + "元");
            }
        } else {
            // 套餐充值
            if (goods.getPrice() != null && new BigDecimal(goods.getPrice()).compareTo(BigDecimal.ZERO) > 0) {
                price = goods.getPrice();
            } else {
                return ApiResult.fail("充值套餐配置错误，请联系客服人员");
            }
        }
        
        if(payType == 1 && (StringUtils.isBlank(agentSettings.getAlipayAppid()) || StringUtils.isBlank(agentSettings.getAlipayPublicKey())
        		|| StringUtils.isBlank(agentSettings.getApplicationPrivateKey()))) {
        	return ApiResult.fail("暂不支持支付宝扫码支付，请选择其他支付方式");
        }
        
        if(payType == 8 && (StringUtils.isBlank(agentSettings.getWechatGateway()) || StringUtils.isBlank(agentSettings.getWechatAppid())
        		|| StringUtils.isBlank(agentSettings.getWechatMchid()) || StringUtils.isBlank(agentSettings.getWechatKey())
       		 || StringUtils.isBlank(agentSettings.getWechatpayNotify()))) {
        	return ApiResult.fail("暂不支持微信扫码支付，请选择其他支付方式");
        }

        // 5.调用支付宝接口获取二维码链接
        String orderNo = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + RandomUtil.randomNumbers(6);
        String qrCodeString = getQrCodeString(agentSettings, payType, orderNo, price, goods.getName(), ip);
        if(StringUtils.isBlank(qrCodeString)) {
        	 return ApiResult.fail("扫码失败，请联系客服人员");
        }
        
        // 6.保存未支付订单
        CustomerOrderRecord customerOrderRecord = new CustomerOrderRecord();
        customerOrderRecord.setId(snowflake.nextId());
        customerOrderRecord.setAgentId(agentSettings.getAgentId()).setCreateTime(new Date())
                .setUpdateTime(customerOrderRecord.getCreateTime()).setCustomerId(ThreadLocalContainer.getCustomerId())
                .setGoodsName(goods.getName()).setGoodsPrice(price).setGoodsSpecifications(specifications)
                .setOutOrderId(orderNo).setTradeStatus(AlipayTradeStatusEnum.WAIT_BUYER_PAY.getCode()).setVersion(0)
                .setCategory(goods.getCategory()).setPayType(payType);
        int i = this.save(customerOrderRecord);
        if (i<=0) {
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            return ApiResult.ok(MapUtil.builder().put("qrCodeString", qrCodeString).put("orderNo", orderNo).build());
        }

    }
    
    private String getQrCodeString(AgentSettings agentSettings,Integer payType,String orderNo,String price,String goodsName,String ip) throws Exception {
    	if(RechargeStateEnum.ALIPAY_QRCODE.getCode() == payType) {
    		return alipayUtil
                    .getAlipayQrCodeString(agentSettings.getAlipayAppid(), agentSettings.getApplicationPrivateKey(),
                            agentSettings.getAlipayPublicKey(), orderNo, price, goodsName,
                            agentSettings.getAgentId().toString());
    	}else if(RechargeStateEnum.WEIXIN_QRCODE.getCode() == payType) {
    		return weixinPayService.createNativeOrder(new BigDecimal(price), orderNo, goodsName, ip);
    	}else {
    		return null;
    	}
    }

    /**
     * 客户支付宝支付交易记录-新增
     * @date 2021/11/8
     * @param customerOrderRecord
     * @return void
     */
    private int save(CustomerOrderRecord customerOrderRecord) {
        int i = customerOrderRecordMapper.save(customerOrderRecord);
        if (i<=0) {
            logger.error("客户id：{}，新增客户扫码付支付交易记录失败。customerOrderRecord:{}", ThreadLocalContainer.getCustomerId(), customerOrderRecord);
        } else {
            logger.info("客户id：{}，新增客户扫码付支付交易记录成功。customerOrderRecord:{}", ThreadLocalContainer.getCustomerId(), customerOrderRecord);
        }
        return i;
    }



    /**
     * 客户支付宝支付交易记录-查询（通过外部订单号）
     * @date 2021/11/8
     * @param outTradeNo
     * @return CustomerOrderRecord
     */
    public CustomerOrderRecord findByOutTradeNo(String outTradeNo) {
        return customerOrderRecordMapper.findByOutOrderId(outTradeNo);
    }

    /**
     * 客户支付宝支付交易记录-修改
     * @date 2021/11/8
     * @param customerOrderRecord
     * @return void
     */
    public int update(CustomerOrderRecord customerOrderRecord) {
        int i = customerOrderRecordMapper.update(customerOrderRecord);
        if (i<=0) {
            logger.error("客户id：{}，客户支付宝支付交易记录修改失败。customerOrderRecord:{}", customerOrderRecord.getCustomerId(), customerOrderRecord);
        } else {
            logger.info("客户id：{}，客户支付宝支付交易记录修改成功。customerOrderRecord:{}", customerOrderRecord.getCustomerId(), customerOrderRecord);
        }
        return i;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderRecord> getWaitingToPayList() throws Exception {
        CustomerOrderRecord query = new CustomerOrderRecord()
                .setTradeStatus(AlipayTradeStatusEnum.WAIT_BUYER_PAY.getCode());
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(90);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<CustomerOrderRecord> list = customerOrderRecordMapper.findByTradeStatusAndCreateTime(AlipayTradeStatusEnum.WAIT_BUYER_PAY.getCode(), date);
        return list;
    }
}
