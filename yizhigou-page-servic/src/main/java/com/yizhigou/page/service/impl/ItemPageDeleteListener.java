package com.yizhigou.page.service.impl;

import com.yizhigou.com.yizhigou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class ItemPageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;



    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;

        //获取消息内容
        try {
            Long[] goodsId = (Long[]) objectMessage.getObject();
            //执行删除
            itemPageService.deleteItemHtml(goodsId);

            System.out.println("删除成功！！！");


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
