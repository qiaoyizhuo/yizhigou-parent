/*app.service("contentService",function($http){
    //查询大广告位
    this.findByCategoryId=function(categoryId){
        return $http.get("content/findByCategoryId.do?categoryId="+categoryId)
    }
})*/
app.service("contentService",function($http){
    //根据分类ID查询广告列表
    this.findByCategoryId=function(categoryId){
        return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
    }
});