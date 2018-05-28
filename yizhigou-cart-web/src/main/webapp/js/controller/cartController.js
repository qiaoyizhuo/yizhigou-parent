//购物车控制层
app.controller('cartController',function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                //调用计算价格的方法
                $scope.totalValue=cartService.sum($scope.cartList);
            }
        );
    }
    //添加商品到购物车
    $scope.addGoodsToCartList=function(itemId,num){
        cartService.addGoodsToCartList(itemId,num).success(
            function(response){
                if(response.success){
                    $scope.findCartList();//刷新查询购物车列表
                }else{
                    alert(response.message);//弹出错误提示
                }
            }
        );
    }

    //获取地址列表
    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function(response){
                $scope.addressList=response;
                //设置默认地址选择
                //1.循环判断找到isdefault属性等于1的这条数据
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
                //2.找到之后把这条数据 给address
            }
        );
    }
    //选择地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }
    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }


    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }

    //保存订单
    $scope.submitOrder=function() {
        //数据补全，收获地址
        $scope.order.receiverAreaName=$scope.address.address;
        //手机
        $scope.order.receiverMobile=$scope.address.mobile;
        //收件人
        $scope.order.receiver=$scope.address.contact;
        //调用service
        cartService.submitOrder( $scope.order ).success(
            function (response) {
                //保存成功
                if(response.success){
                    //跳转到支付页面
                    //判断支付方式是微信支付
                    if($scope.order.paymentType=='1'){
                        location.href="pay.html"
                    }else{
                        //货到付款
                        location.href="paysuccess.html"
                    }
                }else{
                    alert(response.message)//也可以跳转到提示页面
                }
            }



        )



    }








});
