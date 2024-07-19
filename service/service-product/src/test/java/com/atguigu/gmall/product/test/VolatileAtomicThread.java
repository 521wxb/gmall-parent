package com.atguigu.gmall.product.test;

import java.util.concurrent.atomic.AtomicInteger;

public class VolatileAtomicThread implements Runnable {

    // 定义一个int类型的变量
    private int count = 0 ;

    // private Object object = new Object() ;

    private AtomicInteger atomicInteger = new AtomicInteger() ;

    @Override
    public void run() {

        // 对该变量进行++操作，100次
        for(int x = 0 ; x < 100 ; x++) {
            int integerAndIncrement = atomicInteger.incrementAndGet() ;
            System.out.println("count =========>>>> " + integerAndIncrement);
        }

    }

}
