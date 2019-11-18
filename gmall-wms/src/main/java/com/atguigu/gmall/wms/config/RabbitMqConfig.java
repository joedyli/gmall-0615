package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Exchange exchange(){

        return new TopicExchange("WMS-EXCHANGE", true, false, null);
    }

    @Bean
    public Queue queue(){

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "WMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "wms.ttl");
        arguments.put("x-message-ttl", 120000);
        return new Queue("WMS-TTL-QUEUE", true, false, false, arguments);
    }

    @Bean
    public Binding binding(){

        return new Binding("WMS-TTL-QUEUE", Binding.DestinationType.QUEUE, "WMS-EXCHANGE", "wms.unlock", null);
    }

    @Bean
    public Queue deadQueue(){
        return new Queue("WMS-DEAD-QUEUE", true, false, false, null);
    }

    @Bean
    public Binding deadBinding(){
        return new Binding("WMS-DEAD-QUEUE", Binding.DestinationType.QUEUE, "WMS-EXCHANGE", "wms.ttl", null);
    }
}
