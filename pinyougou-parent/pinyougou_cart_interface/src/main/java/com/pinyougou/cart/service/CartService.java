package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    /**
     * 添加商品到购物车
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     *  从 redis 中查询购物车
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);


    /**
     *  将购物车保存到 redis
     * @param username
     * @param cartList
     * @return
     */
    void saveCartListToRedis(String username,List<Cart>cartList);


    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
