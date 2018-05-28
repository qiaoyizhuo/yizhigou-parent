app.controller('searchController',function($scope,$http,searchService,$location){

    //获取主页传入的参数
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();

    }

    //搜索
    $scope.search=function(){
        //数据类型转换
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo) ;


        searchService.search( $scope.searchMap).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                //分页显示的方法
                buildPageLabel();
            }
        );
    }




    //构建分页栏
    buildPageLabel=function(){
        //循环页码
        $scope.pageLabel=[];
        //获取总页数
        var maxPageNum=$scope.resultMap.totalPages;
        //开始页码
        var firstPage=1;
        //截至页码，翻到最后不可以在翻
        var lastPage=maxPageNum;
//  为了显示省略号
        $scope.firstDot=true;//前面的点
        $scope.lastDot=true;//后面的点
        //页面翻页标签显示
        if ($scope.resultMap.totalPages > 5) {//如果总页数大于5页，进行显示
            //如果当前页小于等于3
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;//显示前五页
                //前面没有省略号
                $scope.firstDot = false;
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {//最后五页
                firstPage = $scope.resultMap.totalPages - 4;
                //后面没省略号
                $scope.lastDot = false;
            } else {
                //显示当前页为中心的 5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        } else {
            $scope.firstDot = false;//前面没点
            $scope.lastDot = false;//后面没点
        }
        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }


    //根据页码查询
    $scope.queryByPage=function(pageNo){
        //页码验证
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    //判断当前页为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }

    //判断当前页是否未最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }


    //排序
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

        //创建所搜对象
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};//搜索对象

    $scope.addSearchItem=function (key,value) {
        if(key=='category' || key=='brand'||key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    //撤销选项
    $scope.removeSearchItem=function (key) {
        if(key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]='';
        }else{
            //直接移除规格项
            delete $scope.searchMap.spec[key];//移除规格项
        }
        $scope.search();
    }


    //如果搜索的是品牌隐藏品牌类表
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }



});