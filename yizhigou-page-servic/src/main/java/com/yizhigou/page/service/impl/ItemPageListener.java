package com.yizhigou.page.service.impl;

import com.yizhigou.com.yizhigou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class ItemPageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        
        //获取message中的信息
        TextMessage textMessage = (TextMessage) message;

        try {
            //取出消息内容
            Long goodsId = Long.parseLong(textMessage.getText());
            //生成静态页面
            itemPageService.genItemHtml(goodsId);
            System.out.println("静态页面生成成功！！！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
