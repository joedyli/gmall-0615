package com.atguigu.gmall.oms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.dao.OrderItemDao;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.feign.GmallPmsClient;
import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;
import org.springframework.util.CollectionUtils;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public OrderEntity createOrder(OrderSubmitVO submitVO) {

        // 新增订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(submitVO.getOrderToken());
        orderEntity.setMemberId(submitVO.getUserId());
        orderEntity.setTotalAmount(submitVO.getTotalPrice());
        orderEntity.setPayType(submitVO.getPayType());
        orderEntity.setCreateTime(new Date());
        orderEntity.setSourceType(1);
        orderEntity.setStatus(0);
        orderEntity.setDeliveryCompany(submitVO.getDeliveryCompany());
        orderEntity.setAutoConfirmDay(15);
        orderEntity.setModifyTime(orderEntity.getCreateTime());
        orderEntity.setConfirmStatus(0);
        orderEntity.setDeleteStatus(0);
        // 根据订单明细查询营销信息获取成长积分和赠送积分 TODO
        orderEntity.setGrowth(100);
        orderEntity.setIntegration(200);
        orderEntity.setMemberUsername(submitVO.getUserName());
        // 查询营销信息： TODO  店铺 spu sku 品类

        MemberReceiveAddressEntity address = submitVO.getAddress();
        if (address != null) {
            orderEntity.setReceiverCity(address.getCity());
            orderEntity.setReceiverDetailAddress(address.getDetailAddress());
            orderEntity.setReceiverName(address.getName());
            orderEntity.setReceiverPhone(address.getPhone());
            orderEntity.setReceiverPostCode(address.getPostCode());
            orderEntity.setReceiverProvince(address.getProvince());
            orderEntity.setReceiverRegion(address.getRegion());
        }

        this.save(orderEntity);

        // 新增订单详情
        List<OrderItemVO> orderItemVOS = submitVO.getOrderItemVOS();
        if (!CollectionUtils.isEmpty(orderItemVOS)) {
            orderItemVOS.forEach(itemVO -> {
                Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(itemVO.getSkuId());
                SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setSkuQuantity(itemVO.getCount());
                itemEntity.setSkuPic(skuInfoEntity.getSkuDefaultImg());
                itemEntity.setSkuName(skuInfoEntity.getSkuTitle());
                itemEntity.setSkuId(itemVO.getSkuId());
                itemEntity.setSpuId(skuInfoEntity.getSpuId());
                itemEntity.setOrderSn(submitVO.getOrderToken());
                itemEntity.setOrderId(orderEntity.getId());
                itemEntity.setCategoryId(skuInfoEntity.getCatalogId());
                itemEntity.setSkuAttrsVals(JSON.toJSONString(itemVO.getSkuAttrValue()));
                itemEntity.setSkuPrice(skuInfoEntity.getPrice());
                // TODO
                orderItemDao.insert(itemEntity);
            });
        }

        this.amqpTemplate.convertAndSend("OMS-EXCHANGE", "oms.close", submitVO.getOrderToken());

        return orderEntity;
    }

    @Override
    public int closeOrder(String orderToken) {
        return this.orderDao.closeOrder(orderToken);
    }

    @Override
    public int success(String orderToken) {
        return this.orderDao.success(orderToken);
    }

}