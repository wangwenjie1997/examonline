package com.wwj.examonline.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendQQMailUtil {

    @Autowired
    private JavaMailSender jms;

    public void send(String sender,String receiver,String title,String text){
        //建立邮件消息
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        //发送者
        log.info("发送者 ----"+sender);
        mainMessage.setFrom(sender);
        //接收者
        log.info("接收者 ----"+receiver);
        mainMessage.setTo(receiver);
        //发送的标题
        log.info("发送的标题 ----"+title);
        mainMessage.setSubject(title);
        //发送的内容
        log.info("发送的内容 ----"+text);
        mainMessage.setText(text);
        jms.send(mainMessage);
    }

}
