package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制层
 */
@RestController
@RequestMapping("/pay")
public class payController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //到 redis 查询支付日志
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断支付日志存在否
        if (seckillOrder != null) {
            long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);
            return weixinPayService.createNative(seckillOrder.getId() + "", fen + "");
        } else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Result result = null;
        //设置超时时间为 5 分钟 初始化
        int x = 0;

        while (true) {
            //调用查询接口
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {//出错
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x > 10) {
                result = new Result(false, "二维码超时");
                //1.调用微信的关闭订单接口
                Map<String, String> payresult = weixinPayService.closePay(out_trade_no);
                //如果返回结果是正常关闭
                if (!"SUCCSEE".equals(payresult.get("result_code"))) {

                    if ("ORDERPAID".equals(payresult.get("err_code"))) {
                        result = new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }
                }
                if (result.isSuccess() == false) {
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;

            }
        }
        return result;
    }


}
