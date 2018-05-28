//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    //添加商品到购物车
    this.addGoodsToCartList=function(itemId,num){
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    //求合计
    this.sum=function (cartList) {
        //定义一个对象,用来存放购买的数量和总价格
        var totalValue={totalNum:0,totalMoney:0.00};
        for (var i = 0; i < cartList.length; i++){
            var cart = cartList[i]//循环出得每个购物车
            for (var j=0;j<cart.orderItemList.length;j++){
                var orderItem = cart.orderItemList[j]//购物车明细
                //计算总数量
                totalValue.totalNum+=orderItem.num;
                //计算总价格
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        return totalValue;
    }

    //获取地址列表
    this.findAddressList=function(){
        return $http.get('address/findListByLoginUser.do');
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }


});
