package com.yizhigou.page.service.impl;

import com.yizhigou.com.yizhigou.page.service.ItemPageService;
import com.yizhigou.mapper.TbGoodsDescMapper;
import com.yizhigou.mapper.TbGoodsMapper;
import com.yizhigou.mapper.TbItemCatMapper;
import com.yizhigou.mapper.TbItemMapper;
import com.yizhigou.pojo.TbGoods;
import com.yizhigou.pojo.TbGoodsDesc;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceimpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    //
    @Override
    public boolean genItemHtml(Long goodsId) {

        //读取模版文件
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            //找到模版，
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel = new HashMap<>();
            //查询数据库，查出商品spu信息  tbgoods表
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //把商品信息赋值给添加到模版中
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);


            //生成面表屑获取商品的商品类目
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();//一级标签的名称
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();//二级标签的名称
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();//三级标签的名称
            //存到域中
            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);

            //查询sku表信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//审核通过的商品
            criteria.andGoodsIdEqualTo(goodsId);
            //采用排序
            example.setOrderByClause("is_default desc");//默认选项
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);

            //拼接生成的文件名称
            Writer out = new FileWriter(pagedir + goodsId + ".html");
            template.process(dataModel,out);

            //关流
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //删除静态页面
    @Override
    public boolean deleteItemHtml(Long[] goodsId) {

        //使用File类的方法
        try {
            for (Long id:goodsId){
                new File(pagedir+id+".html").delete();
            }
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
