
//首页控制器
app.controller('loginController',function ($scope, loginService) {
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }
})