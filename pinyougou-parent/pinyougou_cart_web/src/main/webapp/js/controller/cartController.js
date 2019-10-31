//购物车控制层
app.controller('cartController', function ($scope, cartService) {

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (reponse) {
                $scope.cartList = reponse;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        )
    }

    //添加商品到购物车

    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (reponse) {
                if (reponse.success) {
                    $scope.findCartList(); //刷新列表

                } else {
                    alert(reponse.message);//弹出错误信息
                }
            }
        )
    }


    //加载地址
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (reponse) {
                $scope.addressList = reponse;

                //设置默认地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }

            }
        )
    }

    //选择地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

//判断是否是当前选中的地址
    $scope.isSelectedAddress = function (address) {
        if (address == $scope.address) {
            return true
        } else {
            return false;
        }

    }

    //付款方式选择
    $scope.order = {paymentType: 1}

    $scope.selectPayType = function (type) {
        $scope.order.paymentType=type;

    }

    //保存订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success){
                    //页面跳转 判断是什么支付
                    if($scope.order.paymentType=="1"){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else {//如果货到付款，跳转到提示页面
                        location.href =="paysuccess.html"

                    }
                }else {
                    alert(response.message)
                }
            }
        )
        
    }

});