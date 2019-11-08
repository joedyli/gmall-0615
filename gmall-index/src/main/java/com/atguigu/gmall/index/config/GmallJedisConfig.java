package com.atguigu.gmall.index.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class GmallJedisConfig {

    @Bean
    public JedisPool jedisPool(){

        return new JedisPool("172.16.116.100", 6379);
    }
}
