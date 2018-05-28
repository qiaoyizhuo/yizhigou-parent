package com.yizhigou;

import com.yizhigou.demo.QueueProducer;
import com.yizhigou.demo.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//测试
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-producer.xml")
public class TopicTestQueue {

    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void testProducer(){
        topicProducer.sendTextMessage("2014年topic发送！！！");
    }
}
