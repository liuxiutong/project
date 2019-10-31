package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList (){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");

        if (cartListString==null || cartListString.equals("")) {
            cartListString="[]";
        }

        //未登录的收藏夹
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        if (username.equals("anonymousUser")){//如果未登录
            //从cookie中提取购物车
            System.out.println("从cookie中提取购物车");
            return cartList_cookie;
        }else {//如果已登录
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);//从 redis 中提取
            //合并购物车
          if (cartList_cookie.size()>0){
              //得到合并后的购物车
              List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
              //将合并后的购物车存入redis
              cartService.saveCartListToRedis(username,cartList);
              //本地购物车清除
              util.CookieUtil.deleteCookie(request,response, "cartList");
              return cartList;
          }

            return cartList_redis;
        }


    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")//跨域注解
    public Result addGoodsToCartList(Long itemId,Integer num){
        //允许跨域
        /*response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials","true");*/


        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<Cart> cartList = findCartList();//读取购物车容器
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")){//如果是未登录，保存到 cookie
                util.CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
                System.out.println("向 cookie 存入数据");

            }else {//如果是已登录，保存到 redis
                System.out.println("向 Redis 存入数据");
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
