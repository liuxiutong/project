package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.IntemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private IntemPageService itemPageService;


    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener监听接收到消息..."+goodsIds);
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("网页删除结果："+b);
        } catch (Exception e) {


        }

    }
}
