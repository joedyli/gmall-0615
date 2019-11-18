package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-10-28 16:30:21
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    public List<WareSkuEntity> checkStore(@Param("skuId") Long skuId, @Param("count") Integer count);

    public int lock(@Param("id") Long id, @Param("count") Integer count);

    public int unlock(@Param("id") Long id, @Param("count") Integer count);

    void minus(@Param("id") Long id, @Param("count") Integer count);
}
