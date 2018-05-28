package com.yizhigou;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Component
public class Consumer {

    @JmsListener(destination="jd")
    public void readMessage(String text){
        System.out.println("接收到消息："+text);
    }

    @JmsListener(destination = "jd_map")
    public  void  readMap(Map map){
        System.out.println(map);
    }
}
