package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    Map closePay(String out_trade_no);

    /**
     * 生成二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative (String out_trade_no,String total_fee);

    /**
     * 查询支付状态
     * @param out_trade_no
     */
    public Map queryPayStatus(String out_trade_no);

}
