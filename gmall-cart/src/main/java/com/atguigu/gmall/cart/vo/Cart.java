package com.atguigu.gmall.cart.vo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {

    private Boolean check;
    private Long skuId;// 商品id
    private String title;// 标题
    private String defaultImage;// 图片
    private BigDecimal price;// 加入购物车时的价格
    private BigDecimal currentPrice; // 当前价格
    private Integer count;// 购买数量
    private List<SkuSaleAttrValueEntity> skuAttrValue;// 商品规格参数
    private List<ItemSaleVO> sales;
}
