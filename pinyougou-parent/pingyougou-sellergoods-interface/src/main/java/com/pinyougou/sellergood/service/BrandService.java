package com.pinyougou.sellergood.service;


import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;


/**
 * 品牌接口
 */
public interface BrandService {

    List<TbBrand> findAll();

    /**
     * 品牌分页
     *
     * @param pageNum  当前页面
     * @param pageSize 每页记录数
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);

    /**
     * 添加Brand
     *
     * @param brand
     */
    void add(TbBrand brand);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    TbBrand findOne(long id);

    /**
     * 添加数据
     * @param tbBrand
     */
    void update(TbBrand tbBrand);

    /**
     * 删除多个
     * @param ids
     */
    void delete(long[] ids);


    /**
     * 删除单个
     * @param id
     */
    void delete(long id);


    /**
     * 品牌分页+条件查询
     * @param brand 条件参数
     * @param pageNum 当前页面
     * @param pageSize 每页记录数
     * @return
     */
    PageResult findPage(TbBrand brand, int pageNum, int pageSize);


}
