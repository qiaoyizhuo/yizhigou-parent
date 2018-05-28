package com.yizhigou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yizhigou.mapper.*;
import com.yizhigou.pojo.*;
import com.yizhigou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;






	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//0 未审核 1未通过 2审核通过
		//保存商品
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//保存商品sku列表
		goodsDescMapper.insert(goods.getGoodsDesc());

		//启动规格参数
		saveItemList(goods);
	}

	//抽离保存商品的方法
	private void setItemValue(TbItem item,Goods goods){

		//查询商品所属类目
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategoryid(goods.getGoods().getCategory3Id());
		//商品分类名称
		item.setCategory(tbItemCat.getName());
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		//获取商品的id
		item.setGoodsId(goods.getGoods().getId());
		//获取商家id
		item.setSellerId(goods.getGoods().getSellerId());
		//根据id去品牌表进行查询
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(tbBrand.getName());
		//存储商家店铺名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());

		//图片保存
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
		if(imageList.size()>0){
			item .setImage((String) imageList.get(0).get("url"));
		}
	}


	//抽取添加新的sku列表数据
	private void saveItemList(Goods goods){
		//启动规格参数
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			//商品sku表
			for(TbItem item:goods.getItemList()){

				//拼接item表中的title属性
				//sku标题
				String title = goods.getGoods().getGoodsName();
				//spc规格项参数
				Map<String,Object> specmap = JSON.parseObject(item.getSpec());
				for(String key : specmap.keySet()){
					title+=" "+specmap.get(key);
				}
				//拼接后的标题
				item.setTitle(title);
				//调取封装的方法
				setItemValue(item,goods);
				itemMapper.insert(item);
			}
		}else{
			TbItem item = new TbItem();
			//拼接item表中的title属性
			//sku标题
			String title = goods.getGoods().getGoodsName();
			//价格
			item.setPrice(goods.getGoods().getPrice());
			//设置模版项
			item.setSpec("{}");
			//设置库存为9999
			item.setNum(9999);
			//设置状态为1
			item.setStatus("1");
			//拼接后的标题
			item.setTitle(title);

			//调取封装的方法
			setItemValue(item,goods);

			itemMapper.insert(item);
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//修改后状态修改为未审核
		goods.getGoods().setAuditStatus("0");
		//保存商品表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//保存商品扩展表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//修改sku列表//先删除   后添加
		TbItemExample example=new TbItemExample();
		com.yizhigou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		//删除
		itemMapper.deleteByExample(example);
		//添加新的sku列表数据
		saveItemList(goods);//插入商品SKU列表数据
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){//id == 当前商品的id
		//查询两张表 tb_goods  tb_goodsDesc
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		Goods goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		//读取sku列表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);

		goods.setItemList(tbItems);


		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		TbGoodsExample.Criteria criteria = example.createCriteria();
		//非删除状态
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");



				//商户查询自己的店铺商品
				criteria.andSellerIdEqualTo(goods.getSellerId());



			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	/**
	 * 批量修改状态
	 * @param
	 * @param status
	 */

	public void updateStatus(Long[] ids, String status) {
		//根据商品id查询
		for(Long id: ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			//修改状态
			tbGoods.setAuditStatus(status);
			//修改数据库
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}


	/**
	 * 根据商品ID和状态查询Item表信息
	 */

	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {

		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		//id在这个传入的数据中
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}

}
