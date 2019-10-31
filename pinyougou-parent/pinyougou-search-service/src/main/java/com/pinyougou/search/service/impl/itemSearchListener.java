package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * 监听接收消息
 */
@Component
public class itemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String textList = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(textList, TbItem.class);
            /*格式转换加赋值*/
            for (TbItem item : itemList) {
                String spec = item.getSpec();
                //将spec字段中的json字符串转换为map
                Map jsonMap = JSON.parseObject(spec);
                item.setSpecMap(jsonMap);
            }
            /*掉方法*/
            itemSearchService.importList(itemList);

        } catch (JMSException e) {


        }
    }
}
