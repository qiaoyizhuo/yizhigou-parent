package com.yizhigou.test;

import com.yizhigou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-solr.xml")
public class TestSolr {

    @Autowired
    private SolrTemplate solrTemplate;

//添加索引库数据
    @Test
    public void testAdd(){
        TbItem item = new TbItem();
        item.setId(1L);
        item.setGoodsId(20L);
        item.setTitle("华为P20");
        item.setPrice(new BigDecimal(20));
        item.setImage("aaa.jpg");
        //添加进去
        solrTemplate.saveBean(item);
        //提交事物
        solrTemplate.commit();

    }

    //查询单条
    @Test
    public void quertById(){
        TbItem item = solrTemplate.getById(3, TbItem.class);
        System.out.println(item.getTitle());
    }


    //按主键删除
    @Test
    public void deleteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    //先添加点虚假数据
    @Test
    public void testAddAll(){
        List<TbItem> list = new ArrayList<>();
        for(int i=0;i<100;i++){
            TbItem item = new TbItem();
            item.setId(1L+i);
            item.setGoodsId(20L);
            item.setTitle("华为P20"+i);
            item.setPrice(new BigDecimal(2000+i));
            item.setImage("aaa.jpg");
            list.add(item);
        }
        //添加进去
        solrTemplate.saveBeans(list);
        //提交事物
        solrTemplate.commit();
    }

    //分页查询
    @Test
    public void queryPage(){
        //查询全部
        Query query = new SimpleQuery("*:*");
        //开始条数
        query.setOffset(20);
        //每页显示的条数
        query.setRows(20);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query,TbItem.class);
        System.out.println("总条数----："+tbItems.getTotalElements());
        List<TbItem> content = tbItems.getContent();
        showTbItem(content);
    }


    //条件查询
    @Test
    public void queryTest(){
        Query query =new SimpleQuery("*:*");
        //添加条件
        Criteria criteria=new Criteria("item_title").contains("2");
        criteria=criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        //开始条数
        query.setOffset(20);
        //每页显示的条数
        query.setRows(20);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query,TbItem.class);
        System.out.println("总条数----："+tbItems.getTotalElements());
        List<TbItem> content = tbItems.getContent();
        showTbItem(content);

    }

    //全部删除
    @Test
    public void deleteAll(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //打印
    public void showTbItem(List<TbItem> list){
        for(TbItem tbItem:list){
            System.out.println("标题--："+tbItem.getTitle()+"+++价格"+tbItem.getPrice());
        }
    }
}
