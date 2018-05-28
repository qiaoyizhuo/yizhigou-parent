package com.yizhigou.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @RequestMapping("/showName")
    public  String showName(){
        //获取登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return name;
    }
}
