//服务层
app.service("seckillGoodsService",function ($http) {

	//提交订单
	this.submitOrder = function (seckillId) {
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }

	this.findOne = function (id) {
		return $http.get("../seckillGoods/findOneFromRedis.do?id="+id);
    }

	this.findList = function () {
        //读取列表数据绑定到表单中
		return $http.get("../seckillGoods/findList.do")
    }
})