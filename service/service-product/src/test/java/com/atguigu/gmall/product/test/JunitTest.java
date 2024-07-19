package com.atguigu.gmall.product.test;

import org.junit.Assert;
import org.junit.Test;

public class JunitTest {

    @Test
    public void test() {

        HelloTest helloTest = new HelloTest() ;
        int add = helloTest.add(1, 1);
        // System.out.println(add);
        Assert.assertEquals(4 , add);

    }

}
