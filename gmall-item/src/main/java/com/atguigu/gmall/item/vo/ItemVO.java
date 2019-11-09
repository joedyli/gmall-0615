package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.util.List;

@Data
public class ItemVO extends SkuInfoEntity {

    private BrandEntity brand;
    private CategoryEntity category;
    private SpuInfoEntity spuInfo;

    private List<String> pics; // sku的图片列表

    private List<ItemSaleVO> sales; // 营销信息

    private Boolean store; // 是否有货

    private List<SkuSaleAttrValueEntity> skuSales; // spu下所有sku的销售属性

    private SpuInfoDescEntity desc; // 描述信息

    private List<GroupVO> groups; // 组及组下的规格属性及值
}
