package com.atguigu.gmall.pms.vo;

import lombok.Data;

@Data
public class SpuAttributeValueVO {
    private Long productAttributeId; //当前sku对应的属性的attr_id
    private String name;//属性名  电池
    private String value;//3G   3000mah
}
