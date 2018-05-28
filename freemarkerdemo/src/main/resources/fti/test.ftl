<html>

<head>
    <title>freemarker小程序</title>

    <meta charset="UTF-8">
</head>

    <body>

    <#assign linkman="周先生"/>

     ${linkman} 您好，欢迎使用freemarker静态技术

    <br>
    <#assign  info={"id":"100","ename":"岳慧慧","age":"23"}/>
    id:${info.id}<br>
    名称:${info.ename}<br>
    年龄:${info.age}<br>
    <br>
    <#include "head.ftl"/>

    <br>
    <#if success=true>
        有
    <#else >
        没有
    </#if >
    ----商品价格表----<br>

    <table>
        <tr>
            <td>商品名称</td>
            <td>商品价格</td>
        </tr>
        <#list list as goods>
            <tr>
                <td>${goods.name}</td>
                <td>${goods.price}</td>
            </tr>
        </#list>
    </table>
    共多少条记录${list?size}
    <br>
    <#assign  text="{'bank':'工商银行','account':'1231231453578626'}">

    <#assign data=text?eval />

    开户行：${data.bank},账号:${data.account}

    <br><br>
    ${today?date}
    <br><br>
    ${today?time}
    <br><br>
    ${today?datetime}
    <br><br>
    ${today?string("yyyy年MM月")}

    <br><br>
    <#if aaa??>
        aaa有值
    <#else >
        aaa不存在

    </#if>
    <br><br>
    ${aaa!"存在"}

    </body>
</html>