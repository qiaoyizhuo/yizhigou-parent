package com.yizhigou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)//单元测试注解
@ContextConfiguration(locations="classpath:spring/applicationContext-redis.xml")
public class TestString {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void setValue(){
        redisTemplate.boundValueOps("name").set("chtjava");
    }


    @Test
    public void getValue(){
        String name = (String) redisTemplate.boundValueOps("name").get();
        System.out.println(name);
    }
}
