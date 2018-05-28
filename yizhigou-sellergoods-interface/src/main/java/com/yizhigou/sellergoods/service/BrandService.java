package com.yizhigou.sellergoods.service;

import entity.PageResult;
import com.yizhigou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    //全查
    public List<TbBrand> findAll();

    //分页查询
    PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    //保存
    void save(TbBrand brand);

    //单查
    TbBrand fingOne(long id);

    void update(TbBrand tbBrand);

    //删除
    void delete(Long[] id);

    //单查下拉选项
    List<Map> selectOptionList();
}
