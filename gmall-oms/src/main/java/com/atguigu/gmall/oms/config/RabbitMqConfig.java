package com.atguigu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Exchange exchange(){

        return new TopicExchange("OMS-EXCHANGE", true, false, null);
    }

    @Bean
    public Queue queue(){

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "OMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "oms.dead");
        arguments.put("x-message-ttl", 110000);
        return new Queue("OMS-TTL-QUEUE", true, false, false, arguments);
    }

    @Bean
    public Binding binding(){

        return new Binding("OMS-TTL-QUEUE", Binding.DestinationType.QUEUE, "OMS-EXCHANGE", "oms.close", null);
    }

    @Bean
    public Queue deadQueue(){
        return new Queue("OMS-DEAD-QUEUE", true, false, false, null);
    }

    @Bean
    public Binding deadBinding(){
        return new Binding("OMS-DEAD-QUEUE", Binding.DestinationType.QUEUE, "OMS-EXCHANGE", "oms.dead", null);
    }
}
