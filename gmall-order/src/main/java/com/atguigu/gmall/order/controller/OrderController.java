package com.atguigu.gmall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.config.AlipayTemplate;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.PayAsyncVo;
import com.atguigu.gmall.order.vo.PayVo;
import com.atguigu.gmall.order.vo.SeckillVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("confirm")
    public Resp<OrderConfirmVO> confirm(){

        OrderConfirmVO orderConfirmVO = this.orderService.confirm();

        return Resp.ok(orderConfirmVO);
    }

    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVO orderSubmitVO){

        String form = null;
        try {
            OrderEntity orderEntity = this.orderService.submit(orderSubmitVO);

            PayVo payVo = new PayVo();
            payVo.setBody("谷粒商城支付系统");
            payVo.setSubject("支付平台");
            payVo.setTotal_amount(orderEntity.getTotalAmount().toString());
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            form = this.alipayTemplate.pay(payVo);
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return Resp.ok(form);
    }

    @PostMapping("/pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        System.out.println("=======================支付成功==========================");
        // 订单状态的修改和库存的扣除
        orderService.paySucess(payAsyncVo.getOut_trade_no());

        return Resp.ok(null);
    }

    @RequestMapping("seckill/{skuId}")
    public Resp<Object> seckill(@PathVariable("skuId")Long skuId) throws InterruptedException {

        // 查询秒杀库存
        String stockJson = this.redisTemplate.opsForValue().get("seckill:stock:" + skuId);
        if (StringUtils.isEmpty(stockJson)){
            return Resp.ok("该秒杀不存在！");
        }

        Integer stock = Integer.valueOf(stockJson);

        RSemaphore semaphore = this.redissonClient.getSemaphore("seckill:lock:" + skuId);
        semaphore.trySetPermits(stock);

        semaphore.acquire(1);

        UserInfo userInfo = LoginInterceptor.get();

        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:count:" + userInfo.getUserId());
        countDownLatch.trySetCount(1);

        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSkuId(skuId);
        seckillVO.setUserId(userInfo.getUserId());
        seckillVO.setCount(1);

        this.amqpTemplate.convertAndSend("SECKILL-EXCHANGE", "seckill.create", seckillVO);

        this.redisTemplate.opsForValue().set("seckill:stock:" + skuId, String.valueOf(--stock));

        return Resp.ok(null);
    }

    @GetMapping
    public Resp<OrderEntity> queryOrder() throws InterruptedException {
        UserInfo userInfo = LoginInterceptor.get();

        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:count:" + userInfo.getUserId());
        countDownLatch.await();

//        OrderEntity orderEntity = this.orderService.queryOrder();

        return Resp.ok(null);
    }
}
