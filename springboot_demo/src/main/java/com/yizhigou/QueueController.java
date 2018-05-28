package com.yizhigou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueueController {

    /**
     * 消息生产者
     * @author Administrator
     */

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public void send(String text){
        jmsMessagingTemplate.convertAndSend("jd",text);
    }


    @RequestMapping("/sendMap")
    public void sendMap(){
        Map map=new HashMap<>();
        map.put("mobile", "17600465591");
        map.put("template_code", "SMS_135042031");
        map.put("sign_name","明天的你会感谢现在的你");
        map.put("param", "{\"name\":\"8888\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);
    }
}
