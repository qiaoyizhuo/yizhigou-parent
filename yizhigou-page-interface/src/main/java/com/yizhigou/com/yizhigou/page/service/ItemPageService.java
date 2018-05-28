package com.yizhigou.com.yizhigou.page.service;

public interface ItemPageService {

    //生成静态页面
    public boolean genItemHtml(Long goodsid);

    //删除静态页面
    public boolean deleteItemHtml(Long[] goodsId);
}
