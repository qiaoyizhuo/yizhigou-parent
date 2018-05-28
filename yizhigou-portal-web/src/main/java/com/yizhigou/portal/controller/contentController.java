package com.yizhigou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yizhigou.content.service.ContentService;
import com.yizhigou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/***
 *广告内容管理
 */
@RestController
@RequestMapping("/content")
public class contentController {


    //根据列别id查询广告类别
    @Reference
    private ContentService contentService;

    @RequestMapping("findByCategoryId")
    public List<TbContent> findByCategoryId(long categoryId){

        return contentService.findByCategoryId(categoryId);
    }

}
