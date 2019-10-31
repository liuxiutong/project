app.controller('brandController', function ($scope, $http, $controller, brandService) {

    $controller('baseController', {$scope: $scope});
    //查询品牌数据
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };


    $scope.findPage = function (page, size) {
        brandService.findPage(page, size).success(
            function (response) {
                $scope.list = response.rows;//显示当前页数据
                $scope.paginationConf.totalItems = response.total;//更新记录总数
            }
        );
    };

    //新增&修改
    $scope.save = function () {
        var object = null;//方法名
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//调用刷新方法；
                } else {
                    alert(response.message);//提示错误
                }
            }
        );
    };

    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };


    //批量删除
    $scope.dele = function () {

        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.searchEntity = {};
    //条件查询
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;//显示当前页数据
                $scope.paginationConf.totalItems = response.total;//更新记录总数
            }
        );
    };


});