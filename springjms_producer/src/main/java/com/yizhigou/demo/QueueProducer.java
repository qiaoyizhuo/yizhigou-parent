package com.yizhigou.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class QueueProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    //确定消息的类型
    @Autowired
    private Destination queueTextDestination;

    /**
     * 发送文本消息
     */
    public void sendTextMessage(final String text){

        jmsTemplate.send(queueTextDestination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                //添加进消息内容
                return session.createTextMessage(text);
            }
        });
    }

}
