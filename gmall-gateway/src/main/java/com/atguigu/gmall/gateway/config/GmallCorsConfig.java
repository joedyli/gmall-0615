package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GmallCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){

        // cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:1000"); // 允许那些域名跨域请求
        configuration.addAllowedOrigin("http://localhost:2000"); // 允许那些域名跨域请求
        configuration.addAllowedHeader("*"); // 允许跨域请求携带的头信息
        configuration.addAllowedMethod("*"); // 允许跨域请求的方法
        configuration.setAllowCredentials(true); // 是否允许携带cookie信息

        // cors配置源
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(configurationSource);
    }
}
