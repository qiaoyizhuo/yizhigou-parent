package com.yizhigou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yizhigou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 搜索
 */
@RestController
@RequestMapping("/itemsearch")
public class SearchController {


    @Reference
    private SearchService searchService;


    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map searchMap){
        return searchService.search(searchMap);
    }




}
