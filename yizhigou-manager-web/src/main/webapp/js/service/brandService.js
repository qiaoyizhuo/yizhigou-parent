app.service("brandService",function ($http) {
    //查询单挑
    this.findOne=function(id){
        return $http.get('../brand/findOne.do?id='+id);
    }
    //删除
    this.dele=function(ids){
        return  $http.get('../brand/delete.do?ids='+ids);
    }
    //添加
    this.add=function(entity){
        return $http.post('../brand/add.do',entity);
    }
    //修改
    this.update=function(entity){
        return $http.post('../brand/update.do',entity);
    }
    //查询
    this.search=function(entity,page,rows){
        return $http.post('../brand/search.do?page='+page+"&rows="+rows,entity);
    }
    
    //查询品牌列表
    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    }
})