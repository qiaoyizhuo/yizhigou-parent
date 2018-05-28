//控制层
app.controller('seckillGoodsController' ,function($scope,seckillGoodsService,$location,$interval){
    //读取列表数据绑定到表单中
    $scope.findList=function(){
        seckillGoodsService.findList().success(
            function(response){
                $scope.list=response;
            }
        );
    }


    //根据ID获取实体(从缓存中读取)制作详情页
    $scope.findOne=function () {
        seckillGoodsService.findOneFromRedis($location.search()['id']).success(
            function (response) {
                $scope.entity=response;
                //获取结束时间时间-当前时间   剩余秒
                //Math.floor获取到整数
                allsecond=Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000);//获取到秒数
                time=$interval(function () {
                    allsecond = allsecond-1;
                    $scope.timeString=converTimeString(allsecond);
                    if(allsecond<=0){ 
                        //结束秒杀时间数
                        $interval.cancel(time); 
                    }
                },1000)
            }
        )
    }


    //转换秒为   天小时分钟秒格式  XXX天 10:22:33
    converTimeString=function(allsecond){
        //天  小时   分钟
        var days = Math.floor(allsecond/(60*60*24));//天数
        var hours = Math.floor((allsecond-days*60*60*24)/(60*60));//小时数
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        var timeString="";
        if(days>0){
            timeString=days+"天 ";
        }
        return timeString+hours+":"+minutes+":"+seconds;
    }

    //提交订单
    $scope.submitOrder=function(){
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function(response){
                if(response.success){
                    alert("下单成功，请在1分钟内完成支付");
                    location.href="pay.html";
                }else{
                    alert(response.message);
                    location.href="aaa.html";
                }
            }
        );
    }

});
