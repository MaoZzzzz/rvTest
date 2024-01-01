package com.example.rvptest.util;

import com.example.rvptest.config.HttpPoolConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @Description: HTTP线程池工具类
 * @Author: Mzz
 * @Date: 2023/12/9 22:09
 */
@Component
public class HttpClientPoolUtils {
    private HttpPoolConfig httpPoolProperties;

    /**
     * @Description 实例化一个连接池管理器
     * @Param none
     * @Return none
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", SSLConnectionSocketFactory.getSocketFactory()).build();

        PoolingHttpClientConnectionManager httpClientConnectionManager =
            new PoolingHttpClientConnectionManager(registry);

        httpClientConnectionManager.setMaxTotal(httpPoolProperties.getMaxTotal());
        httpClientConnectionManager.setDefaultMaxPerRoute(httpPoolProperties.getDefaultMaxPerRoute());
        httpClientConnectionManager.setValidateAfterInactivity(httpPoolProperties.getValidateAfterInactivity());

        return httpClientConnectionManager;
    }

    /**
     * @Description 获取httpClient
     * @Param
     * @Return HttpClientBuilder
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(
        @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);

        if (httpPoolProperties.isEnableRetry()) {
            httpClientBuilder
                .setRetryHandler(new DefaultHttpRequestRetryHandler(httpPoolProperties.getRetryTimes(), true));
        } else {
            httpClientBuilder.disableAutomaticRetries();
        }
        // 另外httpClientBuilder 可以设置长连接策略，dns解析器，代理，拦截器以及UserAgent等等。可根据业务需要进行实现

        return httpClientBuilder;
    }

    /**
     * @Description 注入连接池
     * @Param
     * @Return CloseableHttpClient
     */
    @Bean("httpClient")
    public CloseableHttpClient httpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(httpPoolProperties.getConnectTimeout()) // 连接上服务器(握手成功)的时间，超出抛出connect timeout
            // 从连接池中获取连接的超时时间，超时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for
            // connection from pool
            .setConnectionRequestTimeout(httpPoolProperties.getConnectionRequestTimeout())
            // 服务器返回数据(response)的时间，超过抛出read timeout
            .setSocketTimeout(httpPoolProperties.getSocketTimeout());
    }

    /**
     * 使用builder构建一个RequestConfig对象
     * 
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }

    /**
     * RestTemplate 指定httpClient 及连接池
     *
     * @param httpClient
     * @return
     */
    @Bean(name = "httpClientTemplate")
    public RestTemplate restTemplate(@Qualifier("httpClient") CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        return restTemplate;
    }

    @Autowired
    public void setHttpPoolProperties(HttpPoolConfig httpPoolProperties) {
        this.httpPoolProperties = httpPoolProperties;
    }
}
