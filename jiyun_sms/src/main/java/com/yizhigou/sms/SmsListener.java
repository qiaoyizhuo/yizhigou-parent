package com.yizhigou.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;
/**
 * 消息监听类
 * @author Administrator
 */
@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    /**
     * 消息监听类
     * @author Administrator
     */
    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){

        try {
            //String mobile,String template_code,String sign_name,String param
           SendSmsResponse response =  smsUtil.sendSms(map.get("mobile"),//手机号
                    map.get("template_code"),//模版编号
                    map.get("sign_name"),//签名
                    map.get("param"));//内容

            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());


        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


}
