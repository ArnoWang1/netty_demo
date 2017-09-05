package com.nan.netty.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExample {

    public static void main(String[] args) throws Exception {
        //初始化线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //异步任务1
        Runnable task1 = () -> System.out.println("This is task1");
        //异步任务2
        Callable<Integer> task2 = () -> {
            Thread.sleep(2000L);
            return 1314;
        };
        //提交任务
        Future<?> f1 = executor.submit(task1);
        Future<Integer> f2 = executor.submit(task2);

        System.out.println("task1 is completed? " + f1.isDone());

        System.out.println("task2 is completed? " + f2.isDone());

        //等待task1完成
        while (f1.isDone()) {
            System.out.println("task1 completed.");
            break;
        }
        //等待task2完成
        System.out.println("task2返回值: " + f2.get());
        executor.shutdown();
    }

}