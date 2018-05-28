package com.yizhigou.demo;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


public class TopicMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("获取到的信息====="+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
