package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProductAttrValueVO extends ProductAttrValueEntity {

    public void setValueSelected(List<String> valueSelected){

        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
