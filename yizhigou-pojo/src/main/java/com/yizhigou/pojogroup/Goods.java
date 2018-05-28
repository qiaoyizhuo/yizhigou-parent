package com.yizhigou.pojogroup;

import com.yizhigou.pojo.TbGoods;
import com.yizhigou.pojo.TbGoodsDesc;
import com.yizhigou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;
/*
* 关于商品信息的表，三张表写到一个实体类中
* **/
public class Goods implements Serializable {

    private TbGoods goods;//商品spu
    private TbGoodsDesc goodsDesc;//商品扩展属性
    private List<TbItem> itemList;//商品sku列表

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
