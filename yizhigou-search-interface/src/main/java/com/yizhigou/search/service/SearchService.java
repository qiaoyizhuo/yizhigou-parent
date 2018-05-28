package com.yizhigou.search.service;

import java.util.List;
import java.util.Map;

public interface SearchService {

    /**
     * 搜索
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 导入数据
     */
    public void importList(List list);

    /**
     * 删除数据
     */
    public void deleteByGoodsIds(List goodsIdList);

}
