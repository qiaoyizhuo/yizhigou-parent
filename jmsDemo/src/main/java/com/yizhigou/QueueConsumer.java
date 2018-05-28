package com.yizhigou;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class QueueConsumer {

    public static void main(String[] args) throws Exception {
        //1。创建连接工程                                                              //tcp是一个协议
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.177.131:61616");
        //2。获取连接
        Connection connection = connectionFactory.createConnection();
        //3。启动连接
        connection.start();
        //4。获取session  (参数1：是否启动事务,参数2：消息确认模式)
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5。创建队列对象
        Queue queue = session.createQueue("test-queue");
        //6。创建消息消费
        MessageConsumer consumer = session.createConsumer(queue);
        //7。监听消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    //获取到的消息
                    String str = textMessage.getText();
                    System.out.println(str+"+++++++++++获取到的消息");

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //8。等待键盘输入
        System.in.read();
        //9。关闭资源
        consumer.close();
        session.close();
        connection.close();
    }
}
