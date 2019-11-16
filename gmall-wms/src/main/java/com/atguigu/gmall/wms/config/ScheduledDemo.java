package com.atguigu.gmall.wms.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledDemo {

    @Scheduled(fixedDelay = 10000)
    public void test(){
        System.out.println("================="  + LocalDateTime.now());
    }
}
