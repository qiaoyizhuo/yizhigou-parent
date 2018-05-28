package com.yizhigou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yizhigou.cart.service.CartService;
import com.yizhigou.mapper.TbItemMapper;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.pojo.TbOrderItem;
import com.yizhigou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 购物车服务实现类
 * @author Administrator
 *
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;


    @Override
    // num购买商品数量
    // 购物车信息
    // itemId商品id
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断:
        if(item==null){
            throw  new RuntimeException("商品不存在");//运行时异常
        }
        if(!item.getStatus().equals("1")){
            throw  new RuntimeException("商品状态无效");//运行时异常
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList,sellerId);
        //4.如果购物车列表中不存在该商家的购物车
        if(cart==null){
            //4.1 新建购物车对象
            cart = new Cart();
            //这只是同一店铺的购物车对象
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //创建订单明细
            TbOrderItem orderItem = createOrderItem(item,num);
            List list = new ArrayList();
            //添加到商品购物车明细中
            list.add(orderItem);
            //放入商家购物车列表
            cart.setOrderItemList(list);
            //4.2将购物车对象添加到购物车列表
            cartList.add(cart);//cart是购物车里同一店铺的 商品，
            //cartList是所有的商品购车车
        }else{//5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            // 如何进行比较，新传进来的数据和已经存在的购物车信息进行比较（两个list的比较）
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), item.getId());
            if(orderItem==null){
                //5.1. 如果没有，新增购物车明细
                orderItem = createOrderItem(item,num);
                //添加到购物车明细里
                cart.getOrderItemList().add(orderItem);

            }else{
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                //更改价格
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*num));
                //如果购物车数量操作后小于等于0，移除
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果移除后cart的明细数量为0，则将cart移除
                if(cart.getOrderItemList().size()==0){
                    //如果移除后，移除商家信息
                    cartList.remove(cart);
                }

            }

        }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从redis中取
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中取出了数据。。。。。。。");

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);

        if(cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }


    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("购物车存入redis中。。。。。。。");
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }
    /**
     * 合并购物车
     * 合并商品商家
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        System.out.println("合并购物车。。。。。");
        for (Cart cart:cartList2){
            //获取购物车信息
            for (TbOrderItem orderItem:cart.getOrderItemList()){
                //集合1  和  集合2 进行合并 ，如果集合里已经有的数据在集合2中数量进行修改
                cartList1 = addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
                //合并之后的集合
            }
        }
        return cartList1;
    }


    /**
     * 根据商家ID查询购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    public  Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for(Cart cart:cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return  null;
    }

    /**
     * 创建订单明细
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num<=0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        //sku商品id
        orderItem.setItemId(item.getId());
        //spu商品id
        orderItem.setGoodsId(item.getGoodsId());
        //购买数量
        orderItem.setNum(num);
        //购买总价
        orderItem.setPrice(item.getPrice());
        //购买商品图片
        orderItem.setPicPath(item.getImage());
        //购买用户id
        orderItem.setSellerId(item.getSellerId());
        //购买商品名称
        orderItem.setTitle(item.getTitle());
        //购买商品小记
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }


    /**
     * 根据商品明细ID查询
     * @param orderItemList，商品明细
     * @param itemId，//商品sku
     * @return  判断购物车是否有该商品
     */
    //两个list中是否有相同的数据
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList ,Long itemId ){

        for(TbOrderItem orderItem :orderItemList){
            //.longValue比较的是基本数据类型，否则比较的是存储地址
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;

    }

}
