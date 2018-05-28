 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService,$location){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	
	//查询实体 
	$scope.findOne=function(id){
		//想获取页面什么信息，就可以在[]中输入属性名称
		var id = $location.search()['id']
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;//
				//富文本编辑器回显数据  添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表------将图片列表由字符串转换为json集合对象
                $scope.entity.goodsDesc.itemImages= JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //显示规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//显示sku表 spec属性
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}



            }
		);				
	}



    //根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		//获取到当前所以选项
		var items = $scope.entity.goodsDesc.specificationItems;
		//判断是否被选中
		var object = $scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return false;
		}else{
			//判断是否包含，包含选中
			if(object.attributeValue.indexOf(optionName)>0){
				return true;
			}else{
				return false;
			}

		}

	}


    //保存
    $scope.save=function(){
        //获取富文本编辑器里的内容
        $scope.entity.goodsDesc.introduction=editor.html();

        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            //清空富文本编辑器
            $scope.entity={};
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    //$scope.reloadList();//重新加载
					location.href="goods.html"
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //保存
	/*$scope.save=function(){
		//获取富文本编辑器里的内容
		$scope.entity.goodsDesc.introduction=editor.html();

		goodsService.add( $scope.entity ).success(//增加
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
					alert(response.message);
					//清空富文本编辑器
					$scope.entity={};
					editor.html('');
				}else{
					alert(response.message);
				}
			}		
		);				
	}*/
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


    //上传图片（商家后台）
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
                //如果上传成功，取出url
				if(response.success){
					$scope.image_entity.url=response.message;//设置文件地址

				}else{
					alert(response.message);
				}
            }
		).error(function() {
            alert("上传发生错误");
        });
    }




    //页面初始化
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};








    //添加图片
	//添加图片列表
	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    //移除图片
	$scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }


    //三级目录列表

    //读取一级分类
    $scope.selectItemCat1List=function(){
        itemCatService.findByParentId(0).success(
            function(response){
                $scope.categoey1List=response;
            }
        );
    }
    //读取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		//根据选择的值，查询二级分类
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.categoey2List=response;
            }
		)
    })
    //读取三级分类
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		//根据选择的值，查询二级分类
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.categoey3List=response;
            }
		)
    })
    //查询三级页面，进行操作
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {

		//根据选重的id，获取当前类目的id
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
            }
		)
    })
    //弹出模版id后，查询品牌列表
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {

		//根据选重的id调用findOne()，获取当前类目的id
		typeTemplateService.findOne(newValue).success(
			function (response) {
                $scope.typeTemplate = response;
                //将查询查的  品牌信息  数据赋予页面		//将字符串转换为JSON
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                //将查询查的  模版信息  数据赋予页面				//将字符串转换为JSON
                if ($location.search()['id'] == null) {//如果有值就不显示，没值就显示
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);

                }
            }
		)

        //选中模版id后跟新模版对象
        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function(response){
                $scope.specList=response;
            }
        );

    });


	//判断是否选中
	$scope.updateSpecAttribute=function ($event,name,value) {
		//定义一个对象
		var object =$scope.searchObjectByKey(
			$scope.entity.goodsDesc.specificationItems,'attributeName',name
		)
		//如果不为空则说明有参数
		if(object!=null){
			//判断是否被选中
			if($event.target.checked){
                //新建一个
                object.attributeValue.push(value);
			}else{
				//取消选中
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				//选项取消勾选之外
				if(object.attributeValue.length==0){
					//都取消的将object归零
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}

		}else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
    }


    ////创建SKU列表
    $scope.createItemList=function(){
		//定义一个对象  初始化他
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];
		//定义items
		var items = $scope.entity.goodsDesc.specificationItems;//获取规格项列表
		for(var i=0;i<items.length;i++){
			//调用克隆方法
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
    }

    //进行克隆
    addColumn=function (list,columnName,columnValues) {
        //定义一个新集合
        var newList=[];
        for(var i=0;i<list.length;i++){
            var oldRow=list[i];
            for(var j=0;j<columnValues.length;j++){
                var newRow = JSON.parse(JSON.stringify(oldRow))

                //进行克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

	//显示状态
    $scope.status=["未审核","审核通过","审核未通过","审核驳回"];


	//定义一个数组
	$scope.itemCatList=[];

	//显示分类
	$scope.findItemCatList=function () {
		itemCatService.findAll().success(
			function (response) {
				//$scope.list=response;
				//把JSON数据便利出来，放到一个对象中
				for(var i=0;i<response.length;i++){

					//将item_cat表中的id和name添加到一个数组中
					$scope.itemCatList[response[i].id]=response[i].name;
				}
            }
		)
    }









	});
