package com.yizhigou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.yizhigou.mapper.TbSpecificationOptionMapper;
import com.yizhigou.pojo.TbSpecificationExample;
import com.yizhigou.pojo.TbSpecificationOption;
import com.yizhigou.pojo.TbSpecificationOptionExample;
import com.yizhigou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbSpecificationMapper;
import com.yizhigou.pojo.TbSpecification;
import com.yizhigou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        specificationMapper.insert(specification.getSpecification());
        //获取规格项，保存规格项
        for (TbSpecificationOption specificationOptions : specification.getSpecificationOptionList()) {
            //通过sql返回了添加成功的id
            specificationOptions.setSpecId(specification.getSpecification().getId());//获取到新增后返回的ID
            specificationOptionMapper.insert(specificationOptions);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        specificationMapper.updateByPrimaryKey(specification.getSpecification());//修改规格表
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //获取规格名称的id  通过id删除规格项
        criteria.andSpecIdEqualTo(specification.getSpecification().getId());
        //删除规格项
        specificationOptionMapper.deleteByExample(example);
        //循环添加
        for (TbSpecificationOption specificationOption:specification.getSpecificationOptionList()){
            specificationOption.setSpecId(specification.getSpecification().getId());//获得id
            //循环添加
            specificationOptionMapper.insert(specificationOption);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {

        //1、查询通过 规格id 查询（规格名称）
        //2、查询规格项   写条件 optionList 的里的 specId查询
        //3、查询完之后，再次封装到Specification里 页面返回和遍历
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //添加查询条件
        criteria.andSpecIdEqualTo(id);
        //查询规格项
        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
        //查询规格名称
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        Specification specification = new Specification();
        //相当于存域
        specification.setSpecification(tbSpecification);//规格名称
        specification.setSpecificationOptionList(list);//规格项
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);
            //删除规格项目
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            //删除规格项
            specificationOptionMapper.deleteByExample(example);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();

        TbSpecificationExample.Criteria criteria = example.createCriteria();
        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
