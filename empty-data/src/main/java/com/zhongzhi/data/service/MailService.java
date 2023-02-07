package com.zhongzhi.data.service;

import cn.hutool.json.JSONUtil;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.vo.MailVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 邮件服务类
 */
@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    //注入邮件工具类
    @Resource
    private JavaMailSenderImpl mailSender;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public void sendMail(MailVo mailVo) {
        threadPoolExecutor.execute(()->{
            try {
                //1.检测邮件
                checkMail(mailVo);
                //2.发送邮件
                sendMimeMail(mailVo);
                //3.保存邮件
                saveMail(mailVo);
            } catch (Exception e) {
                mailVo.setStatus("fail");
                mailVo.setError(e.getMessage());
            }
        });
    }

    /**
     * 检测邮件信息类
     */
    private void checkMail(MailVo mailVo) {
        if (StringUtils.isEmpty(mailVo.getTo())) {
            throw new BusinessException("邮件收信人不能为空");
        }
        if (StringUtils.isEmpty(mailVo.getSubject())) {
            throw new BusinessException("邮件主题不能为空");
        }
        if (StringUtils.isEmpty(mailVo.getText())) {
            throw new BusinessException("邮件内容不能为空");
        }
    }

    /**
     * 构建复杂邮件信息类
     */
    private void sendMimeMail(MailVo mailVo) {
        try {
            //true表示支持复杂类型
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
            //邮件发信人从配置项读取
            mailVo.setFrom(getMailSendFrom());
            messageHelper.setFrom(mailVo.getFrom());//邮件发信人
            messageHelper.setTo(mailVo.getTo().split(","));//邮件收信人
            messageHelper.setSubject(mailVo.getSubject());//邮件主题
            messageHelper.setText(mailVo.getText());//邮件内容
            if (!StringUtils.isEmpty(mailVo.getCc())) {//抄送
                messageHelper.setCc(mailVo.getCc().split(","));
            }
            if (!StringUtils.isEmpty(mailVo.getBcc())) {//密送
                messageHelper.setCc(mailVo.getBcc().split(","));
            }
            if (mailVo.getMultipartFiles() != null) {//添加邮件附件
                for (MultipartFile multipartFile : mailVo.getMultipartFiles()) {
                    messageHelper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
                }
            }
            if (StringUtils.isEmpty(mailVo.getSentDate())) {//发送时间
                mailVo.setSentDate(new Date());
                messageHelper.setSentDate(mailVo.getSentDate());
            }
            mailSender.send(messageHelper.getMimeMessage());//正式发送邮件
            mailVo.setStatus("ok");
            logger.info("邮件发送成功：" + JSONUtil.toJsonStr(mailVo));
        } catch (Exception e) {
            logger.error("邮件发送出现异常：\n{}", ExceptionUtils.getStackTrace(e));
            throw new BusinessException("发送邮件出现异常");
        }
    }

    //保存邮件
    private void saveMail(MailVo mailVo) {

        //将邮件保存到数据库..
    }

    //获取邮件发信人
    public String getMailSendFrom() {
        return mailSender.getJavaMailProperties().getProperty("from");
    }

}
