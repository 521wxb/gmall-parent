package com.atguigu.gmall.item.test;

import com.atguigu.gmall.item.ItemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

//@SpringBootTest(classes = ItemApplication.class)
public class CompletableFutureTest {

//    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    /**
     * 多任务的组合
     */
    @org.junit.Test
    public void test11() throws ExecutionException, InterruptedException, IOException {

        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务1执行完毕");
        });


        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2执行完毕");
        });

        // 让completableFuture1和completableFuture2任务都是执行完毕以后，此时在执行下面的代码
        // CompletableFuture.allOf(completableFuture1 , completableFuture2).join();   // 让执行当前test11方法的线程阻塞

        // 让completableFuture1和completableFuture2任意一个任务执行完毕，此时在执行下面的代码
        // CompletableFuture.anyOf(completableFuture1 , completableFuture2).join() ;

        // 让completableFuture1和completableFuture2执行完成以后，在执行另外一个任务
        completableFuture1.runAfterBoth(completableFuture2 , () -> System.out.println("任务3执行完毕")) ;

        // 输出
        System.out.println("任务执行完毕了");

        System.in.read() ;

    }


    // 需求：在控制台按照顺序输出"haha"、"hehe"、"heihei"
    @Test
    public void test10() {
        CompletableFuture.runAsync(() -> System.out.println("haha")).thenRun(() -> System.out.println("hehe"))
                .thenRun(() -> System.out.println("heihei")) ;
    }

    /**
     * thenApply
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test09() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            return 10;
        }).thenApplyAsync((result -> result * 3), threadPoolExecutor);

        Integer integer = completableFuture.get();      // 获取最终的执行结果
        System.out.println(integer);

    }

    /**
     * thenAcceptAsync
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test08() throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            return 10 ;
        }).thenAcceptAsync((result -> System.out.println(result)) , threadPoolExecutor );
    }

    /**
     * thenRun方法
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test07() throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            int a = 1/0 ;
            return 10 ;
        }).thenRunAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行该任务");
        }) ;
    }

    /**
     * whenCompleteAsync上一次任务异常执行完毕了,返回默认值
     */
    @Test
    public void test06() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            int a = 1 / 0 ;
            return 10;
        }).whenCompleteAsync((result, e) -> {
            System.out.println("whenCompleteAsync--->result:" + result);
            System.out.println("whenCompleteAsync--->e:" + e);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return 30 ;
        });

        Integer integer = completableFuture.get();      // 当任务异常执行了，那么此时在调用get方法获取任务的执行结果的时候直接报错
        System.out.println(integer);

    }


    /**
     * whenCompleteAsync上一次任务异常执行完毕了
     */
    @Test
    public void test05() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            int a = 1 / 0 ;
            return 10;
        }).whenCompleteAsync((result, e) -> {
            System.out.println("whenCompleteAsync--->result:" + result);
            System.out.println("whenCompleteAsync--->e:" + e);
        });

        Integer integer = completableFuture.get();      // 当任务异常执行了，那么此时在调用get方法获取任务的执行结果的时候直接报错
        System.out.println(integer);

    }

    /**
     * whenCompleteAsync上一次任务正常执行完毕了
     */
    @Test
    public void test04() throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            return 10;
        }).whenCompleteAsync((result, e) -> {
            System.out.println("whenCompleteAsync--->result:" + result);
            System.out.println("whenCompleteAsync--->e:" + e);
        });

        Integer integer = completableFuture.get();      // 获取任务的最后一次执行结果
        System.out.println(integer);

    }

    /**
     * 测试runAsync，执行一个任务，并且获取异步任务的返回值
     */
    @Test
    public void test03() throws IOException, ExecutionException, InterruptedException {

        System.out.println("执行任务开始了.........");

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
            return 10;
        });

        Integer integer = completableFuture.get(); // get方法会阻塞当前线程
        System.out.println(integer);

        System.out.println("执行任务结束了.........");
        System.in.read() ;
    }

    /**
     * 测试runAsync，执行一个任务，无返回结果
     */
    @Test
    public void test02() throws IOException {

        System.out.println("执行任务开始了.........");

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
        },threadPoolExecutor) ;

        System.out.println("执行任务结束了.........");
        System.in.read() ;
    }

    /**
     * 测试runAsync，执行一个任务，无返回结果
     */
    @Test
    public void test01() throws IOException {

        System.out.println("执行任务开始了.........");

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "---->执行了某一个任务");
        }) ;

        System.out.println("执行任务结束了.........");
        System.in.read() ;
    }

}
