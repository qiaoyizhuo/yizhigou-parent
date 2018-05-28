package com.yizhigou.content.service.impl;
import java.util.List;

import com.yizhigou.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbContentMapper;
import com.yizhigou.pojo.TbContent;
import com.yizhigou.content.service.ContentService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {

		//清空一下缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());


		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		//查询修改前的分类Id
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);
		//如果分类ID发生了修改,清除修改后的分类ID的缓存
		if(categoryId.longValue()!=content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//通过id获取指定广告
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			contentMapper.deleteByPrimaryKey(id);
			//清空一下缓存
			redisTemplate.boundHashOps("content").delete(categoryId);

		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		TbContentExample.Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 按照分类信息查询广告id
	 */
	@Override
	public List<TbContent> findByCategoryId(long categoryId) {

		//1。从redis中去数据   通过id去取
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);

		//2。没有的话，再去数据库查
		if(contentList==null){

			TbContentExample example=new TbContentExample();
			TbContentExample.Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);

			//查询状态为1的
			criteria.andStatusEqualTo("1");
			//排序
			example.setOrderByClause("sort_order");

			contentList = contentMapper.selectByExample(example);

			//3。数据库查询出的东西存到redis中

			redisTemplate.boundHashOps("content").put(categoryId,contentList);

			//4。别人在访问时，redis中就有数据了

		}else{
			System.out.println("从缓存缓读取");
		}
		return contentList;
	}

}
