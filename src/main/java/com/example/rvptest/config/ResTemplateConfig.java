package com.example.rvptest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Description: RestTemplate配置
 * @Author: Mzz
 * @Date: 2023/12/9 20:46
 */
public class ResTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean("urlConnectionTemplate")
    public RestTemplate urlConnectionRestTemplate() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(5000);
        simpleClientHttpRequestFactory.setConnectTimeout(15000);
        return new RestTemplate(simpleClientHttpRequestFactory);
    }

    @Bean("httpClientTemplate")
    public RestTemplate httpClientRestTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Bean("OKHttp3Template")
    public RestTemplate OKHttp3RestTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
