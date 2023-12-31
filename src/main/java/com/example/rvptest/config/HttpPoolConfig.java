package com.example.rvptest.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Description: HTTP线程池配置
 * @Author: Mzz
 * @Date: 2023/12/9 22:07
 */
@Component
@Data
public class HttpPoolConfig {
    // 最大连接数
    private Integer maxTotal = 20;
    // 同路由并发数
    private Integer defaultMaxPerRoute = 20;
    private Integer connectTimeout = 2000;
    private Integer connectionRequestTimeout = 2000;
    private Integer socketTimeout = 2000;
    // 线程空闲多久后进行校验
    private Integer validateAfterInactivity = 2000;
    // 重试次数
    private Integer retryTimes = 2;

    // 是否开启重试
    private boolean enableRetry = true;
    // 重试的间隔：可实现 ServiceUnavailableRetryStrategy 接口
    private Integer retryInterval = 2000;
}
