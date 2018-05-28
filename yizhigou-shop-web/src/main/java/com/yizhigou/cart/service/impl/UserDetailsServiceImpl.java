package com.yizhigou.cart.service.impl;

import com.yizhigou.pojo.TbSeller;
import com.yizhigou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        //分配角色
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //根绝用户名查询用户信息
        TbSeller seller = sellerService.findOne(username);
        if(seller!=null){
            //判断是否审核通过
            if(seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(), grantedAuths);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
}
