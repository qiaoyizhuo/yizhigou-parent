package com.yizhigou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yizhigou.cart.service.CartService;
import com.yizhigou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 6000)
    private CartService cartService;


    /**
     * 购物车列表
     *
     * @param
     * @return
     */
    //显示购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        //获取登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("用户名是："+userName);
        //从cook中取商品信息
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            //为了不报空指针
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString,Cart.class);

        //未登陆，继续存在cookie中
        if(userName.equals("anonymousUser")){
            //读取本地购物车
            System.out.println("通过cookie存储购物车。。。。。。。");

            return cartList_cookie;
        }else{
            //已登陆
            List<Cart> cartList_reids = cartService.findCartListFromRedis(userName);
            //如果本地cookie中存在商品
            if(cartList_cookie.size()>0){
                //合并redis和cookie中的信息
                cartList_reids=cartService.mergeCartList(cartList_reids,cartList_cookie);
                //清除cookie
                CookieUtil.deleteCookie(request,response,"cartList");
                //合并之后的数据保存到redis中
                cartService.saveCartListToRedis(userName, cartList_reids);
            }
            return  cartList_reids;
        }
    }

    /**
     * 添加商品到购物车
     *
     * @param //request
     * @param //response
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:9105")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        /*//解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");//打开cookie操作*/


        //获取登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //1。获取购物车列表
            List<Cart> cartList = findCartList();
            //如果未登录
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if(userName.equals("anonymousUser")){
                //2。存入到cookie
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");

            }else {
                //用户已经等了，从redis中去数据
                cartService.saveCartListToRedis(userName, cartList);
            }

             return new Result(true, "添加购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加购物车失败！");
        }

    }


}
