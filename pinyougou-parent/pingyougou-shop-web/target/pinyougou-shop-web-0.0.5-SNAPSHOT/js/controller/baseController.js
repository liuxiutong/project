app.controller('baseController', function ($scope) {


    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //刷新数据
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };


    $scope.selectIds = [];//用户勾选的ID集合

    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id)//push方法像集合添加元素
        } else {
            var index = $scope.selectIds.indexOf(id);//查找去掉勾选的id的位置
            $scope.selectIds.splice(index, 1);//根据位置删除id，删除一个
        }
    };

    //数据优化
    $scope.jsonToSpring = function (jsonString,key) {

        var json1 = JSON.parse(jsonString);

        var value="";
        for (var i = 0; i < json1.length; i++) {
            if (i > 0) {
                 value += ",";
            }
            value +=json1[i][key];
        }
        return value;

    }

    //从集合中按照key查询对象
    $scope.searchObjectByKey = function (list, key, keyValue) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key]==keyValue) {
                return list[i]
            }
        }
        return null;
    }


});