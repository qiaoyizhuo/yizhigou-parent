package com.yizhigou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {

    //注入service
    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Message message) {
        //通过获取信息
        TextMessage textMessage = (TextMessage) message;

        try {
            String jsonStr = textMessage.getText();//json字符串
            //转换为list
            List<TbItem> itemList = JSON.parseArray(jsonStr, TbItem.class);
            System.out.println("activeMQ开始同步！！");
            //规格项处理
            for (TbItem item:itemList){
                //转换规格项
                Map specMap = JSON.parseObject(item.getSpec());
                item.setSpecMap(specMap);
            }


            searchService.importList(itemList);



        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
