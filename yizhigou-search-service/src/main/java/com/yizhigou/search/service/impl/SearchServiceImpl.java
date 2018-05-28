package com.yizhigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yizhigou.pojo.TbBrand;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




//请求时间等待时间
@Service(timeout = 3000)
public class SearchServiceImpl implements SearchService {



    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 根据关键字搜索列表
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String,Object> map = new HashMap<>();


        //关键字空格处理
        String str = (String) searchMap.get("keywords");
        if(str!=null){
            searchMap.put("keywords",str.replace(" ",""));
        }


       //1。查询列表（高亮显示）查询
        map.putAll(searchList(searchMap));

        //2。根据关键字条件查询
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3。根据分类名称，取出缓存中的品牌列表和规格项列表
        if(categoryList.size()>0){
            map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
        }

        //4。根据分类名称查询对应品牌
        //取出分类名称
        String categoryName = (String) searchMap.get("category");
        if(!"".equals(categoryName)){
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{
            if(categoryList.size()>0) {
                //如果没有，按照品牌下第一个分类名称作为查询条件
                map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
            }
        }

        return map;
    }



    /**
     * 根据关键字搜索列表
     * @return
     */
    private Map searchList(Map searchMap){

        Map map = new HashMap<>();
        //1.设置高亮显示查询
        HighlightQuery query = new SimpleHighlightQuery();

        //1.1按照分类塞选
        if(!"".equals(searchMap.get("category"))){
            //增加一个搜索条件
           /* Criteria criteria = new Criteria("item_category").is(searchMap.get("category"));*/
            Criteria criteria=new Criteria("item_category").is(searchMap.get("category"));
            //添加过滤条件
            FilterQuery filterQuery = new SimpleFacetQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //1.2按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3过滤规格
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map) searchMap.get("spec");
            for(String key:specMap.keySet() ){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                //添加过滤条件
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.4按照价格进行过滤查询
        //传入价格非空验证
        if (!"".equals(searchMap.get("price"))){

            //拆分传入的参数           ///传入参数是0-500
            String[] price = ((String) searchMap.get("price")).split("-");
            //如果第一位不等0
            if(!price[0].equals("0")){
                Criteria criteria =  new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFacetQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
            //3000-*
            if(!price[1].equals("*")){
                Criteria criteria =  new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFacetQuery(criteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //1.5分页
        Integer pageNo = (Integer) searchMap.get("pageNo");//获取当前页
        //默认当前页
        if(pageNo==null){
            pageNo=1;
        }
        //获取每页条数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        //默认当每页显示
        if(pageSize==null){
            pageSize=40;
        }
    //计算出从第几条开始显示
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);


        //1.6排序
        //获取排序方式
        String sortValue= (String) searchMap.get("sort");//ASC  DESC
        //获取排序的字段
        String sortField = (String) searchMap.get("sortField");
        //升序
        if(sortValue.equals("ASC")){
            Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
            query.addSort(sort);
        }
        //降序
        if(sortValue.equals("DESC")){
            Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
            query.addSort(sort);
        }




        //2。高亮显示的字段
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");

        //3。设置高亮显示的前缀和后缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");//后缀
        //4。设置高亮显示的选项
        query.setHighlightOptions(highlightOptions);
        //5。获取高亮显示字段内容

        //按照关键字查------查询来源，配置的动态域
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);


        //循环高亮入口集合
        for (HighlightEntry<TbItem> h:page.getHighlighted()){
            //获取原实例体
            TbItem item = h.getEntity();
            if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                //设置高亮结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",page.getContent());

        //产寻处的数据，总条数放入map
        map.put("totalPages",page.getTotalPages());//总页数
        map.put("total",page.getTotalElements());//总条数
        return map;
    }


    /**
     * 查询商品分类
     * @return
     */
    private List searchCategoryList(Map searchMap){

        ArrayList<Object> list = new ArrayList<>();

        //1.设置查询条件
        Query query = new SimpleQuery();
        //2。设置搜索关键字
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //添加搜索条件
        query.addCriteria(criteria);
        //3。设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //4。得到分组列
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //5。根据分组列得到分组结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //6。根据分组结果集找到入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //7。取出分组结果放入list中
        for(GroupEntry<TbItem> entry:content){
            //获取到分组结果到List
            list.add(entry.getGroupValue());
        }

        return list;
    }


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据分类名称，查询品牌列表和规格项列表
     * @return
     */
    private Map searchBrandAndSpecList(String categoryName){

        Map map =  new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        if(typeId!=null){
            //取出品牌列表
            List<TbBrand> brandList = (List<TbBrand>) redisTemplate.boundHashOps("brandList").get(typeId);
            //放入map集合
            map.put("brandList",brandList);
            //取出规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            //放入map集合
            map.put("specList",specList);
        }
       return map;
    }


    /**
     * 导入数据到solr
     */
    @Override
    public void importList(List list) {
        //开始导入到solr
        solrTemplate.saveBeans(list);
        solrTemplate.commit();

    }

    /**
     * 删除solr索引库
     */
    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsId").in(goodsIdList);
        query.addCriteria(criteria);
        //删除
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();

    }


}
