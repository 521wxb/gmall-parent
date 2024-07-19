package com.atguigu.gmall.gateway.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

public class ReactorTest {

    /**
     * 响应式编程：当执行多个操作的时候，是不会阻塞线程，每一个操作可以认为是开启一个线程，如果下一个操作想获取上一个操作的结果，那么此时就需要订阅上一个操作
     *           当上一个操作有结果了，那么此时立马执行下一个操作。响应式编程基于发布订阅模式。
     *           发布订阅模式需要一个容器来实现数据传输：Mono ， Mono容器只会存储一个数据。一个线程发布数据到Mono中，另外一个线程订阅Mono，然后Mono中
     *           存在了数据，那么订阅这个Mono的线程就立马执行
     *           Flux： 容器对象，在Flux中可以存储多个
     * 阻塞式编程：通过一个线程完成多个操作，只有一个操作完毕以后才可以进行下一个操作，在执行下一个操作的之前，线程处于阻塞状态
     */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("ReactorTest.......main...");
        // int result = getData();
        // Mono<String> mono = getResultData();
        // mono.subscribe( result -> System.out.println(result) ) ;
        Flux<String> flux = getFluxData();
        flux.subscribe(result -> System.out.println(result)) ;
        System.out.println("ReactorTest.......main...result=");

    }

    public static Flux<String> getFluxData() {
        Flux<String> fromStream = Flux.fromStream(Stream.of("尚硅谷", "西安尚硅谷"));
        return fromStream ;
    }


    public static Mono<String> getResultData() {
        Mono<String> mono = Mono.fromCallable(() -> {
            Thread.sleep(3000);
            return "ok";
        });
        return mono ;
    }

    public static int getData() throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("ReactorTest...getData...");
        return 10 ;
    }

}
