package com.zhongzhi.data.controller.xunlong;

import com.alibaba.fastjson.JSON;
import com.zhongzhi.data.service.NotifyService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * 当面付交易结果异步通知控制器
 * @author rivers
 * @since 2020-02-13
 */
@Slf4j
@RestController
@RequestMapping("/front")
@Api("支付宝当面付交易结果异步通知 API")
public class NotifyController {

    private static final Logger logger = LoggerFactory.getLogger(NotifyController.class);

    @Autowired
    private NotifyService notifyService;

    @RequestMapping("/notify")
    @ResponseBody
    public String alipayNotify(HttpServletRequest request){
        logger.info("支付宝异步通知入参: {}", JSON.toJSONString(request.getParameterMap()));
        return notifyService.alipayNotify(request);
    }
    
    @RequestMapping("/weixinNotify")
    public String weixinNotify(HttpServletRequest request) throws Exception{
    	String paramStr = getBodyContent(request);
        logger.info("微信异步通知入参: {}", paramStr);
        return notifyService.weixinNotify(paramStr);
    }
    
    private String getBodyContent(HttpServletRequest request) {
    	ServletInputStream in = null;
    	BufferedReader reader = null;
    	StringBuilder content = new StringBuilder();
    	try {
			in = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			// 作为输出字符串的临时串，用于判断是否读取完毕
			String itemStr = "";
			while(null != (itemStr = reader.readLine())) {
				content.append(itemStr);
			}
		} catch (Exception e) {
			log.error("",e);
		}finally {
			try {
				if(null != reader) {
					reader.close();
				}
				if(null != in) {
					in.close();
				}
			} catch (Exception e2) {
				log.error("",e2);
			}
		}
    	
    	return content.toString();
    }
}
