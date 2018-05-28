package com.yizhigou.cart.service;

import com.yizhigou.pojogroup.Cart;

import java.util.List;
/**
 * 购物车服务接口
 * @author Administrator
 *
 */
public interface CartService {

    //添加到购物车中，需要的信息
    //商家id，商品id，通过商品id获取商家id
    //商品的数量，商品的购物车明细
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);


    /**
     * 从redis中读取查询购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
