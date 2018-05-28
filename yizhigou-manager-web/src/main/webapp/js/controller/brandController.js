app.controller('brandController',function ($scope,$http,$controller,brandService) {

    $controller('baseController',{$scope:$scope})//继承在这里

    //读取列表数据绑定到表单中
    $scope.findAll=function () {
        $http.get('../brand/findAll.do').success(
            function(response){
                $scope.list=response;
            })
    }

    //添加
    $scope.save=function() {
        var Object = brandService.add($scope.entity);
        if($scope.entity.id!=null){
            Object=brandService.update($scope.entity);
        }

        Object.success(
            function (response) {
                if(response.success){
                    //刷新当前页面
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    }

    //修改
    $scope.findOne=function(id){
        brandService.findOne(id).success(
            function(response){
                $scope.entity = response;
            }
        )
    }



    //向后台发送数据
    $scope.dele=function(){
        brandService.dele($scope.selectIds).success(
            function(response){
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    }


    //定义一个搜索对象
    $scope.searchEntity ={};
    $scope.search=function (page,rows) {
        brandService.search($scope.searchEntity,page,rows).success(
            function(response){
                //更新总条数
                $scope.paginationConf.totalItems=response.total;
                $scope.list=response.rows;
            }
        )
    }
})