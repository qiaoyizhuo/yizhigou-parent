package com.yizhigou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    //读取配置文件
    @Autowired
    private Environment env;

    @RequestMapping("/info")
    public  String showInfo(){
        return "hello World!--------"+env.getProperty("url");
    }
}
