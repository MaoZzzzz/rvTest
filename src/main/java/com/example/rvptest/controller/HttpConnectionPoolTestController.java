package com.example.rvptest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: http连接池效果测试
 * @Author: Mzz
 * @Date: 2023/12/9 21:13
 */
@Slf4j
@RestController
public class HttpConnectionPoolTestController {

    /**
     * 请求结果构造
     */
    private static final StringBuilder EVERY_REQ_COST = new StringBuilder(200);

    private static final String SEPERATOR = "   ";

    private static final AtomicInteger NOW_COUNT = new AtomicInteger(0);

    private static final int REQUEST_COUNT = 20;

    private static final String URL = "http://192.168.0.100:8080/function/compose-post";

    @RequestMapping("/testWithoutPool")
    public void testWithoutPool(HttpServletRequest httpServletRequest) {
        startUpAllThreads(getRunThreads(new HttpThread1()));
        // 等待线程运行
        for (;;);
    }

    @RequestMapping("/testWithPool")
    public void testWithPool(HttpServletRequest httpServletRequest) {
        startUpAllThreads(getRunThreads(new HttpThread2()));
        // 等待线程运行
        for (;;);
    }

    /**
     * @Description 创建发送http请求线程
     * @Param runnable 待处理线程
     * @Param requestCount 请求数
     * @Return List<Thread> 线程集合
     */
    private List<Thread> getRunThreads(Runnable runnable) {
        List<Thread> waitThreadLists = new ArrayList<>(REQUEST_COUNT);

        for (int i = 0; i < REQUEST_COUNT; i++) {
            waitThreadLists.add(new Thread(runnable));
        }

        return waitThreadLists;
    }

    /**
     * @Description 启动所有线程
     * @Param 待启动线程集合
     * @Return none
     */
    private void startUpAllThreads(List<Thread> waitThreadLists) {
        for (Thread thread : waitThreadLists) {
            thread.start();
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    protected synchronized void addCost(long cost) {
        EVERY_REQ_COST.append(cost);
        EVERY_REQ_COST.append("ms");
        EVERY_REQ_COST.append(SEPERATOR);
    }

    private class HttpThread1 implements Runnable {

        @Override
        public void run() {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            HttpGet httpGet = new HttpGet("http://192.168.0.100:8080/function/compose-post");

            long startTime = System.currentTimeMillis();
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                addCost(System.currentTimeMillis() - startTime);

                if (NOW_COUNT.incrementAndGet() == REQUEST_COUNT) {
                    System.out.println(EVERY_REQ_COST.toString());
                }
            }
        }
    }

    @Autowired
    private RestTemplate httpClientTemplate;

    @Autowired
    private CloseableHttpClient httpClient;

    private class HttpThread2 implements Runnable {

        @Override
        public void run() {
            HttpGet httpGet = new HttpGet("http://192.168.0.100:8080/function/compose-post");
            // 长连接标识，不加也没事，HTTP1.1默认都是Connection: keep-alive的
            httpGet.addHeader("Connection", "keep-alive");

            long startTime = System.currentTimeMillis();
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                System.out.println(response);
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                addCost(System.currentTimeMillis() - startTime);

                if (NOW_COUNT.incrementAndGet() == REQUEST_COUNT) {
                    System.out.println(EVERY_REQ_COST.toString());
                }
            }
        }

    }
}
