package com.atguigu.gmall.sms.vo;

import lombok.Data;

@Data
public class ItemSaleVO {

    private String type; // 满减 打折 积分

    private String desc; // 优惠信息的具体描述
}
