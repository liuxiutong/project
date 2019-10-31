package com.pinyougou.page.service;

public interface IntemPageService {

    /**
     * 生成商品详细页
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详细页
     * @param goodsIds
     * @return
     */
    boolean deleteItemHtml(Long[] goodsIds);

}
