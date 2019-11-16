package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CartListener {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CURRENT_PRICE_PRFIX = "cart:price:";

    private static final String KEY_PREFIX = "cart:key:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-CART-QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-ITEM-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.update"}
    ))
    public void listener(Map<String, Object> map){
        Long spuId = (Long)map.get("id");
        Resp<List<SkuInfoEntity>> listResp = this.gmallPmsClient.querySkuBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = listResp.getData();
        skuInfoEntities.forEach(skuInfoEntity -> {
            this.redisTemplate.opsForValue().set(CURRENT_PRICE_PRFIX + skuInfoEntity.getSkuId(), skuInfoEntity.getPrice().toString());
        });
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-CART-DELETE-QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"cart.delete"}
    ))
    public void deleteListener(Map<String, Object> map, Message message, Channel channel) throws IOException {
        String userId = map.get("userId").toString();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);
        List<Long> skuIds = (List<Long>) map.get("skuIds");
        List<String> skuIdString = skuIds.stream().map(skuId -> skuId.toString()).collect(Collectors.toList());
//        int i = 1 / 0;
        hashOps.delete(skuIdString.toArray());
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            e.printStackTrace();
        }
    }
}
