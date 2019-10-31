package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergood.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 品牌分页
     *
     * @param pageNum  当前页面
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);//分页

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);


        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加Brand
     *
     * @param brand
     */
    @Override
    public void add(TbBrand brand) {

            brandMapper.insert(brand);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 添加数据
     *
     * @param tbBrand
     */
    @Override
    public void update(TbBrand tbBrand) {

        brandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 删除多个
     *
     * @param ids
     */
    @Override
    public void delete(long[] ids) {

        for (long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 删除单个
     *
     * @param id
     */
    @Override
    public void delete(long id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 品牌分页+条件查询
     *
     * @param brand    条件参数
     * @param pageNum  当前页面
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);//分页

        TbBrandExample example = new TbBrandExample();

        TbBrandExample.Criteria criteria = example.createCriteria();

        if (brand!=null){
            if (brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);


        return new PageResult(page.getTotal(),page.getResult());

    }



}
