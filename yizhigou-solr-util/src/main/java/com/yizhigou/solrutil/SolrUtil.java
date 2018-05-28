package com.yizhigou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yizhigou.mapper.TbItemMapper;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {



    //把数据库里查询到的数据导入到索引库中
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    //倒入索引库的方法
    public void imporData(){
        //已审核才可以倒入
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //审核通过
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        System.out.println("=====商品列表开始倒入");

        for (TbItem tbItem:tbItems){
            //动态域的倒入
            Map jsonObject = JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSpecMap(jsonObject);
            System.out.println(tbItem.getTitle());
        }
        //倒入到索引库
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("=====商品列表倒入结束");
    }


    public static void main(String[] args){
        //读取spring配置文件
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        //调取执行倒入
        solrUtil.imporData();
    }

}
