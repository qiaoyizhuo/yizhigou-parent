app.controller("indexController",function($scope,$http,loginService) {

    $scope.showLoginName=function () {
        loginService.showLoginName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }
})