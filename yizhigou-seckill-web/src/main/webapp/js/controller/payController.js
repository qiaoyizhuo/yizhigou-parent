app.controller('payController' ,function($scope ,payService,$location) {

    //创建二维码
    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {
                //获取交易金额，页面展示
                $scope.money=(response.total_fee/100).toFixed(2);//转换成0.01
                //订单号
                $scope.out_trade_no= response.out_trade_no;//订单号
                //生成二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                queryPayStatus($scope.out_trade_no);//查询支付状态
            }
        )
    }
    //查询支付状态
    queryPayStatus=function(out_trade_no){
        payService.queryPayStatus($scope.out_trade_no).success(
            function(response){
                if(response.success){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else{
                    if(response.message=='二维码超时'){
                        location.href="payTimeOut.html";
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        );
    }
    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }


})
