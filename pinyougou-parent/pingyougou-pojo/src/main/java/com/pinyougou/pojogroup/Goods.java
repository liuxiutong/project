package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class Goods implements Serializable {
    private TbGoods goods;//商品spu
    private TbGoodsDesc goodsDesc;//商品扩展
    private List<TbItem> itemList;//商品sku列表

    public Goods() {
    }
}
