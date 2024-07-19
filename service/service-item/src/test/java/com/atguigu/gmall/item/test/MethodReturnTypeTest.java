package com.atguigu.gmall.item.test;

import com.atguigu.gmall.web.vo.SkuDetailVo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 研究方法的返回值类型的获取方式
 */
public class MethodReturnTypeTest {

    public SkuDetailVo skuDetailVo() {
        return new SkuDetailVo() ;
    }

    public List<SkuDetailVo> skuDetailVos() {
        return new ArrayList<>() ;
    }

    public Map<String , List<SkuDetailVo>> stringListMap() {
        return new HashMap<>() ;
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {

        // 获取MethodReturnTypeTest字节码文件对象
        MethodReturnTypeTest methodReturnTypeTest = new MethodReturnTypeTest() ;
//        Class clazz01 = methodReturnTypeTest.getClass() ;
//        Class clazz02 = MethodReturnTypeTest.class ;
//        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//        Class clazz04 = systemClassLoader.loadClass("com.atguigu.gmall.item.test.MethodReturnTypeTest");
//        System.out.println(clazz01);
//        System.out.println(clazz02);
//        System.out.println(clazz04);

        Class clazz03 = Class.forName("com.atguigu.gmall.item.test.MethodReturnTypeTest") ;
        Method method = clazz03.getMethod("stringListMap");
        System.out.println(method.getReturnType());         // 只能获取集合类型的返回值类型，不能获取集合泛型类型
        System.out.println(method.getGenericReturnType());  // 获取集合类型的返回值类型，也能获取集合泛型类型

    }

}
