var app = angular.module("myapp",[]);
//创建控制器
app.controller("myController",function($scope,$http){

    $scope.findList=function(){
        $http.get("data.json").success(
            function (response) {
                $scope.list=response;
            }
        );
    }
});