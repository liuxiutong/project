package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.IntemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntemPageServiceImpl implements IntemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {

        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map datamodel = new HashMap<>();

            //1.加载商品表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            datamodel.put("goods", goods);
            //2.加载商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            datamodel.put("goodsDesc", goodsDesc);
            //3.商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            datamodel.put("itemCat1",itemCat1);
            datamodel.put("itemCat2",itemCat2);
            datamodel.put("itemCat3",itemCat3);
            //SKU列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//状态为有效
            criteria.andGoodsIdEqualTo(goodsId);//指定SPU ID
            //按照是否默认降序，保证第一个为默认
            example.setOrderByClause("is_default desc");

            List<TbItem> itemList = itemMapper.selectByExample(example);
            datamodel.put("itemList",itemList);

            Writer out = new FileWriter(pagedir + goods.getId() + ".html");
            template.process(datamodel, out);
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {

        try {
            for (Long goodsId : goodsIds) {
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
