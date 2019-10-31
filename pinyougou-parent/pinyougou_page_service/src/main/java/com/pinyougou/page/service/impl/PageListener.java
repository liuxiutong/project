package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.IntemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {

    @Autowired
    private IntemPageService itemPageService;


    @Override
    public void onMessage(Message message) {
        try {
        TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("接收到消息："+text);
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("生成结果:"+b);
        } catch (JMSException e) {
        }
    }
}
