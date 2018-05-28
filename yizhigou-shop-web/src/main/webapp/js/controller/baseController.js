//控制器：通用的一些方法
app.controller('baseController',function($scope){
    $scope.reloadList=function(){
        //传递两个参数
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    }
    //分页控件
    $scope.paginationConf={
        currentPage:1,//当前页
        totalItems:10,//总条数
        itemsPerPage:10,//每页显示的数量
        perPageOptions:[10,20,30,40,50],//每页显示多少条
        onChange:function(){//加载分页
            $scope.reloadList();
        }
    };

    //删除
    //创建一个数组,这个数组用于村粗选择id
    $scope.selectIds=[];//选择的id在这里存放
    //触发选中的事件
    $scope.updateSelection=function($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            //获取当前选中的这个下标值
            var idx = $scope.selectIds.indexOf(id);//返回当前选中id的下标志
            //移除操作
            $scope.selectIds.splice(idx,1);//第一个参数 移除的下标值  第二个参数 移除的个数
        }
    }

    //提取JSON串中的字符串进行展示
    $scope.jsonToString=function (jsonString,key) {
        var json = JSON.parse(jsonString);//JSON字符串转换为JSON对象
        var value= "";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value += ","
            }
            value += json[i][key];
        }
        return value;
    }

    //传递三个参数   list当前jSON格式  key   keyValue  进行相应的比较
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for(var i=0;i<list.length;i++){
            //通过json串的key值获取对应的value值，与传递进来的value值进行比较，相同不添加
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }


})