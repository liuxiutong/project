package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergood.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goods.getGoods().setAuditStatus("0");//设置未申请状态
        goodsMapper.insert(goods.getGoods());//插入商品表
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置Id
        goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展属性

        saveItemList(goods);
    }
    //add重复代码抽取
    private void setItemValus(Goods goods,TbItem item){

        item.setGoodsId(goods.getGoods().getId());//商品SPU编号
        item.setSellerId(goods.getGoods().getSellerId());//商家编号
        item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期

        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        //图片地址（取spu的第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

        if (imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }
    }

    //提取 插入SKU列表数据
    private void saveItemList(Goods goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                //标题
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());

                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                item.setTitle(title);
                //插入重复的代码
                setItemValus(goods,item);
                itemMapper.insert(item);
            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(999999);//库存数量
            item.setSpec("{}");
            //插入重复的代码
            setItemValus(goods,item);
            itemMapper.insert(item);
        }
    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //设置未申请状态:如果是经过修改的商品，需要重新设置状态
        goods.getGoods().setAuditStatus("0");
        //保存商品表
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //保存商品扩展表
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //(SKU先删除再添加)
        //删除原有的sku列表数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
         criteria.andGoodsIdEqualTo(goods.getGoods().getId());
         itemMapper.deleteByExample(example);
        //添加新的sku列表数据
        saveItemList(goods);//插入商品SKU列表数据



    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        //商品基本表
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //商品扩展表
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //读取SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);//查询条件：商品ID
        List<TbItem> itemlist = itemMapper.selectByExample(example);
        goods.setItemList(itemlist);

        return goods;
    }

    /**
     * 批量删除 (标记为删除 数据还在)
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();//未标记删除的查询
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
               /* criteria.andSellerIdLike("%" + goods.getSellerId() + "%");*/
                criteria.andSellerIdLike( goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample example = new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

}
