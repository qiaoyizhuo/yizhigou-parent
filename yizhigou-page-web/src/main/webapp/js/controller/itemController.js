app.controller("itemController",function($scope,$http){
	//增加购物车数量
	$scope.addNum=function(x){
		$scope.num= $scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}

	//显示规格选项
	$scope.specificationItems={}//记录用户选择的规格

	//用户选择的规格
	$scope.selectSpecification=function(name,value){
		$scope.specificationItems[name]=value;
		//调取方法根据选的的规格，显示title
		searchSku();//读取sku
	}

	//判断用户是否被选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}
	}
	//加载默认的sku表。加载下标为1 的
	$scope.loadSku=function(){
		$scope.sku=skuList[0];//JSON.parse是转化为json对象
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));//这是转换为json字符串


	}
	//根据你选择的获取titla
	//匹配两个对象
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		//进行倒置的判断
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;

	}

	//查询当前sku的方法
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return ;
			}
		}
	}

	//添加商品到购物车
	$scope.addToCart=function(){
		//alert('skuid:'+$scope.sku.id);																		//为了发送cookie请求
		$http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id +"&num="+$scope.num,{'withCredentials':true}).success(
			function (response) {
				if(response.success){
                    //跳转到购物车页面
                    location.href="http://localhost:9107/cart.html"
				}else{
                    alert(response.message);
                }
            }
		)
	}



})