package com.atguigu.gmall.search;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Test
    public void importData(){

        Long pageNum = 1l;
        Long pageSize = 100l;

        do {
            // 分页查询spu
            QueryCondition condition = new QueryCondition();
            condition.setPage(pageNum);
            condition.setLimit(pageSize);
            Resp<List<SpuInfoEntity>> listResp = this.gmallPmsClient.querySpuPage(condition);
            // 获取当前页的spuInfo数据
            List<SpuInfoEntity> spuInfoEntities = listResp.getData();

            // 遍历spu获取spu下的所有sku导入到索引库中
            for (SpuInfoEntity spuInfoEntity : spuInfoEntities) {
                Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsClient.querySkuBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
                if (CollectionUtils.isEmpty(skuInfoEntities)){
                    continue;
                }
                skuInfoEntities.forEach(skuInfoEntity -> {
                    GoodsVO goodsVO = new GoodsVO();

                    // 设置sku相关数据
                    goodsVO.setName(skuInfoEntity.getSkuTitle());
                    goodsVO.setId(skuInfoEntity.getSkuId());
                    goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());
                    goodsVO.setPrice(skuInfoEntity.getPrice());
                    goodsVO.setSale(100); // 销量
                    goodsVO.setSort(0); // 综合排序

                    // 设置品牌相关的
                    Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandById(skuInfoEntity.getBrandId());
                    BrandEntity brandEntity = brandEntityResp.getData();
                    if (brandEntity != null) {
                        goodsVO.setBrandId(skuInfoEntity.getBrandId());
                        goodsVO.setBrandName(brandEntity.getName());
                    }

                    // 设置分类相关的
                    Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
                    CategoryEntity categoryEntity = categoryEntityResp.getData();
                    if (categoryEntity != null) {
                        goodsVO.setProductCategoryId(skuInfoEntity.getCatalogId());
                        goodsVO.setProductCategoryName(categoryEntity.getName());
                    }

                    // 设置搜索属性
                    Resp<List<SpuAttributeValueVO>> searchAttrValueResp = this.gmallPmsClient.querySearchAttrValue(spuInfoEntity.getId());
                    List<SpuAttributeValueVO> spuAttributeValueVOList = searchAttrValueResp.getData();
                    goodsVO.setAttrValueList(spuAttributeValueVOList);

                    // 库存
                    Resp<List<WareSkuEntity>> resp = this.gmallWmsClient.queryWareBySkuId(skuInfoEntity.getSkuId());
                    List<WareSkuEntity> wareSkuEntities = resp.getData();
                    if (wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0)) {
                        goodsVO.setStock(1l);
                    } else {
                        goodsVO.setStock(0l);
                    }

                    Index index = new Index.Builder(goodsVO).index("goods").type("info").id(skuInfoEntity.getSkuId().toString()).build();
                    try {
                        this.jestClient.execute(index);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            pageSize = Long.valueOf(spuInfoEntities.size()); // 获取当前页的记录数
            pageNum++; // 下一页
        } while (pageSize == 100); // 循环条件

    }










    @Test
    void contextLoads() throws IOException {

        Delete build = new Delete.Builder("1").index("user").type("info").build();
        jestClient.execute(build);

//        Index index = new Index.Builder(new User("liuyan", "123456", 18)).index("user").type("info").id("1").build();
//
//        jestClient.execute(index);

//        Map<String, Object> map = new HashMap<>();
//        map.put("doc", new User("bingbing", null, null));
//
//        Update update = new Update.Builder(map).index("user").type("info").id("1").build();
//        DocumentResult result = jestClient.execute(update);
//        System.out.println(result.toString());

//        Get get = new Get.Builder("user", "1").build();
//
//        System.out.println(jestClient.execute(get));

//        String query = "{\n" +
//                "  \"query\": {\n" +
//                "    \"match_all\": {}\n" +
//                "  }\n" +
//                "}";
//        Search search = new Search.Builder(query).addIndex("user").addType("info").build();
//        SearchResult searchResult = jestClient.execute(search);
//        System.out.println(searchResult.getSourceAsObject(User.class, false));
//        List<SearchResult.Hit<User, Void>> hits = searchResult.getHits(User.class);
//        hits.forEach(hit -> {
//            System.out.println(hit.source);
//        });

    }



}

@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    private String name;
    private String password;
    private Integer age;
}
