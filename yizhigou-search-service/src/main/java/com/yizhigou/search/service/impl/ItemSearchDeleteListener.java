package com.yizhigou.search.service.impl;

import com.yizhigou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class ItemSearchDeleteListener implements MessageListener {


    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Message message) {

        //获取数据
        ObjectMessage objectMessage = (ObjectMessage) message;

        try {

            Long[] ids = (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener监听接收到消息..."+ids);
            //删除索引库
            searchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("删除索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
