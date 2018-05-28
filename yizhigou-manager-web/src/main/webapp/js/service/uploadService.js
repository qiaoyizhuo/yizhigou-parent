//文件上传服务层
app.service("uploadService",function ($http) {
    this.uploadFile=function () {
        //定义上传函数
        var formData=new FormData;
        formData.append("file",file.files[0]);
        return $http({
            method:"POST",
            url:"../upload.do",
            data:formData,
            //头文件
            headers:{'Content-Type':undefined},
            //使用angularJS的格式请求
            transformRequest:angular.identity
        })
    }
})