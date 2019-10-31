package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号 ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.参数封装
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");
        param.put("out_trade_no", out_trade_no);//交易订单号
        param.put("total_fee", total_fee);//金额（分）
        param.put("spbill_create_ip", "127.0.0.1");
        param.put("notify_url", "http://www.itcast.cn");
        param.put("trade_type", "NATIVE");//交易类型


        //2.发送请求
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //3.获取结果
            String result = client.getContent();
            System.out.println(result);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            HashMap<String, String> map = new HashMap<>();
            map.put("code_url",resultMap.get("code_url"));//支付地址
            map.put("total_fee",total_fee);////总金额
            map.put("out_trade_no",out_trade_no);//订单号

            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }





    }

    /**
     * 查询支付状态
     * @param out_trade_no
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.参数封装
        Map param=new HashMap();
        param.put("appid", appid);//公众账号 ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";

        //2.发送请求
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //3.获取结果
            String result = client.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(result);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
