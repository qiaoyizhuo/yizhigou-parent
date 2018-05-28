package com.yizhigou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.yizhigou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")////公众账号ID
    private  String appid;

    @Value("${partner}")//商户号
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 生成微信支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //创建参数
        Map<String,String> param = new HashMap<>();
        param.put("appid",appid);//公众账号ID
        param.put("mch_id",partner);//商户号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        param.put("body","易直购");//商品描述
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("total_fee",total_fee);//支付金额
        param.put("spbill_create_ip","127.0.0.1");//终端IP
        param.put("notify_url","www.yizhigou.com");//通知地址
        param.put("trade_type","NATIVE");//交易类型


        //转化xml格式
        try {
            //2.生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("发送数据========="+xmlParam);
            //跨域请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //打开https请求
            client.setHttps(true);
            //发送数据
            client.setXmlParam(xmlParam);
            client.post();
            //接受返回数据
            String result = client.getContent();
            System.out.println("微信返回数据====="+result);

            //转换成Map格式
            Map<String,String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String,String> map = new HashMap<>();
            //获取二维码链接,支付地址
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
            return map;


        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }


    }


    /**
     * 根据订单号查询支付状态
     * @param out_trade_no
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {

        //创建参数
        Map<String,String> param = new HashMap<>();
        param.put("appid",appid);//公众账号ID
        param.put("mch_id",partner);//商户号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        param.put("out_trade_no",out_trade_no);
        try {
            //转换数据转换为xml
           String xmlParam = WXPayUtil.generateSignedXml(param,partnerkey);
            //发送查询信息
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            //设置请求方式
            client.setHttps(true);
            //发送的数据
            client.setXmlParam(xmlParam);
            client.post();
            //获取返回结果
            String result = client.getContent();
            //转换为map格式
            Map<String,String> map = WXPayUtil.xmlToMap(result);
            System.out.println("支付状态结果===="+map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
