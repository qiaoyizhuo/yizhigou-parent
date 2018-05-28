package com.yizhigou.freemarker.test;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class TestFreemarker {

    public static void main(String[] args) throws Exception{

        //第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker的版本号。
        Configuration  configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(new File("/Users/qiaoyizhuo/Documents/yizhigou-parent/freemarkerdemo/src/main/resources/fti"));
        //第三步：设置模板文件使用的字符集。一般就是 utf-8.
        configuration.setDefaultEncoding("UTF-8");
        //第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("test.ftl");
        //第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
        HashMap map = new HashMap<>();
        map.put("names","乔一倬");

        map.put("success",true);


        //测试功能集合
        List list=new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        list.add(goods1);
        list.add(goods2);
        list.add(goods3);
        map.put("list", list);


        map.put("today",new Date());


        //第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
        FileWriter out = new FileWriter("//Users//qiaoyizhuo//Downloads//temp//aaaa.html");
        //第七步：调用模板对象的 process 方法输出文件。
        template.process(map,out);
        //第八步：关闭流
        out.close();
    }
}
