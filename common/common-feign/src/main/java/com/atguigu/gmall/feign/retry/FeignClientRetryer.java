package com.atguigu.gmall.feign.retry;

import feign.RetryableException;
import feign.Retryer;

public class FeignClientRetryer implements Retryer {

    private int startCount = 1 ;
    private int endConut = 3 ;

    @Override
    public void continueOrPropagate(RetryableException e) {
        if(startCount >= endConut) {
            throw e ;
        }
        startCount++ ;
        System.out.println("FeignClientRetryer....continueOrPropagate....");
    }

    @Override
    public Retryer clone() {        // 返回一个FeignClientRetryer对象
        return new FeignClientRetryer();
    }

}
