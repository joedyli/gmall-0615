package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderConfirmVO {

    private List<MemberReceiveAddressEntity> addresses; // 地址列表

    private List<OrderItemVO> orderItems;  // 订单的送货清单

    private Integer bounds; // 积分信息

    private String orderToken; // 防止重复提交

}
