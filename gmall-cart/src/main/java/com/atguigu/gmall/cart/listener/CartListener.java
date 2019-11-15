package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CartListener {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CURRENT_PRICE_PRFIX = "cart:price:";

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
}
