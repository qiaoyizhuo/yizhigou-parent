package com.yizhigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import com.yizhigou.mapper.TbBrandMapper;
import com.yizhigou.pojo.TbBrand;
import com.yizhigou.pojo.TbBrandExample;
import com.yizhigou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService{

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    //分页全查
    @Override
    public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize) {

        //设置分页开始页数和每页显示条数
        PageHelper.startPage(pageNum,pageSize);
        //条件查询的时候放入进来
        TbBrandExample example=new TbBrandExample();
        //生成存条件的方法
        TbBrandExample.Criteria criteria = example.createCriteria();

        //查询条件
         if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
            criteria.andNameLike("%"+tbBrand.getName()+"%");
         }
         if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
             criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
         }


        //调取逆向工程生成的方法
        Page<TbBrand> result = (Page<TbBrand>) brandMapper.selectByExample(example);

        //范湖总条数，查询结果
        return new PageResult(result.getTotal(),result.getResult());
    }

    @Override
    public void save(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand fingOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] id) {
        for(Long ids:id){
            brandMapper.deleteByPrimaryKey(ids);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
