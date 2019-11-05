package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;
import com.atguigu.gmall.search.vo.SearchResponseAttrVO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private JestClient jestClient;

    public SearchResponse search(SearchParamVO searchParamVO) {

        try {
            String dsl = buildDSL(searchParamVO);
            System.out.println(dsl);
            Search search = new Search.Builder(dsl).addIndex("goods").addType("info").build();

            SearchResult searchResult = this.jestClient.execute(search);

            SearchResponse response = parseResult(searchResult);
            // 分页参数
            response.setPageSize(searchParamVO.getPageSize());
            response.setPageNum(searchParamVO.getPageNum());
            response.setTotal(searchResult.getTotal());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SearchResponse parseResult(SearchResult result) {
        SearchResponse response = new SearchResponse();

        // 获取所有聚合
        MetricAggregation aggregations = result.getAggregations();
        // 解析品牌的聚合结果集
        // 获取品牌聚合
        TermsAggregation brandAgg = aggregations.getTermsAggregation("brandAgg");
        // 获取品牌聚合中的所有桶
        List<TermsAggregation.Entry> buckets = brandAgg.getBuckets();
        // 判断品牌聚合是否为空
        if (!CollectionUtils.isEmpty(buckets)){
            // 初始化品牌vo对象
            SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
            attrVO.setName("品牌"); // 写死品牌聚合名称
            List<String> brandValues= buckets.stream().map(bucket -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bucket.getKeyAsString());
                TermsAggregation brandNameAgg = bucket.getTermsAggregation("brandNameAgg"); // 获取品牌id桶中子聚合（品牌的名称）
                map.put("name", brandNameAgg.getBuckets().get(0).getKeyAsString());
                return JSON.toJSONString(map);
            }).collect(Collectors.toList());
            attrVO.setValue(brandValues); // 设置品牌的所有聚合值
            response.setBrand(attrVO);
        }

        // 解析分类的聚合结果集
        TermsAggregation categoryAgg = aggregations.getTermsAggregation("categoryAgg");
        List<TermsAggregation.Entry> catBuckets = categoryAgg.getBuckets();
        if (!CollectionUtils.isEmpty(catBuckets)) {
            SearchResponseAttrVO categoryVO = new SearchResponseAttrVO();
            categoryVO.setName("分类");
            List<String> categoryValues = catBuckets.stream().map(bucket -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bucket.getKeyAsString());
                TermsAggregation categoryNameAgg = bucket.getTermsAggregation("categoryNameAgg");
                map.put("name", categoryNameAgg.getBuckets().get(0).getKeyAsString());
                return JSON.toJSONString(map);
            }).collect(Collectors.toList());
            categoryVO.setValue(categoryValues);
            response.setCatelog(categoryVO);
        }

        // 解析搜索属性的聚合结果集
        ChildrenAggregation attrAgg = aggregations.getChildrenAggregation("attrAgg");
        TermsAggregation attrIdAgg = attrAgg.getTermsAggregation("attrIdAgg");
        List<SearchResponseAttrVO> attrVOS = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
            attrVO.setProductAttributeId(Long.valueOf(bucket.getKeyAsString()));
            // 获取搜索属性的子聚合（搜索属性名）
            TermsAggregation attrNameAgg = bucket.getTermsAggregation("attrNameAgg");
            attrVO.setName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            // 获取搜索属性的子聚合（搜索属性值）
            TermsAggregation attrValueAgg = bucket.getTermsAggregation("attrValueAgg");
            List<String> values = attrValueAgg.getBuckets().stream().map(bucket1 -> bucket1.getKeyAsString()).collect(Collectors.toList());
            attrVO.setValue(values);
            return attrVO;
        }).collect(Collectors.toList());
        response.setAttrs(attrVOS);

        // 解析商品列表的结果集
        List<GoodsVO> goodsVOS = result.getSourceAsObjectList(GoodsVO.class, false);
        response.setProducts(goodsVOS);

        return response;
    }

    private String buildDSL(SearchParamVO searchParamVO) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 1. 构建查询和过滤条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 构建查询条件
        String keyword = searchParamVO.getKeyword();
        if (StringUtils.isNotEmpty(keyword)){
            boolQuery.must(QueryBuilders.matchQuery("name", keyword).operator(Operator.AND));
        }
        // 构建过滤条件
        // 品牌
        String[] brands = searchParamVO.getBrand();
        if (ArrayUtils.isNotEmpty(brands)){
            boolQuery.filter(QueryBuilders.termsQuery("brandId", brands));
        }
        // 分类
        String[] catelog3s = searchParamVO.getCatelog3();
        if (ArrayUtils.isNotEmpty(catelog3s)){
            boolQuery.filter(QueryBuilders.termsQuery("productCategoryId", catelog3s));
        }

        // 搜索的规格属性过滤
        String[] props = searchParamVO.getProps();
        if (ArrayUtils.isNotEmpty(props)) {
            for (String prop : props) {
                String[] attr = StringUtils.split(prop, ":");
                if (attr != null && attr.length == 2) {
                    BoolQueryBuilder propBoolQuery = QueryBuilders.boolQuery();
                    propBoolQuery.must(QueryBuilders.termQuery("attrValueList.productAttributeId", attr[0]));
                    String[] values = StringUtils.split(attr[1], "-");
                    propBoolQuery.must(QueryBuilders.termsQuery("attrValueList.value", values));
                    boolQuery.filter(QueryBuilders.nestedQuery("attrValueList", propBoolQuery, ScoreMode.None));
                }
            }
        }
        sourceBuilder.query(boolQuery);

        // 2. 完成分页的构建
        Integer pageNum = searchParamVO.getPageNum();
        Integer pageSize = searchParamVO.getPageSize();
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);

        // 3. 完成排序的构建
        String order = searchParamVO.getOrder();
        if (StringUtils.isNotBlank(order)){
            String[] orders = StringUtils.split(order, ":");
            if (orders != null && orders.length == 2) {
                SortOrder sortOrder = StringUtils.equals("asc", orders[1]) ? SortOrder.ASC : SortOrder.DESC;
                switch (orders[0]) {
                    case "0": sourceBuilder.sort("_score", sortOrder); break;
                    case "1": sourceBuilder.sort("sale", sortOrder); break;
                    case "2": sourceBuilder.sort("price", sortOrder); break;
                    default: break;
                }
            }
        }

        // 4. 完成高亮的构建
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        sourceBuilder.highlighter(highlightBuilder);

        // 5. 完成聚合条件的构建
        // 品牌
        sourceBuilder.aggregation(
                AggregationBuilders.terms("brandAgg").field("brandId")
                        .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));

        // 分类
        sourceBuilder.aggregation(
                AggregationBuilders.terms("categoryAgg").field("productCategoryId")
                        .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("productCategoryName")));

        // 搜索属性
        sourceBuilder.aggregation(
                AggregationBuilders.nested("attrAgg", "attrValueList")
                        .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrValueList.productAttributeId")
                                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrValueList.name"))
                                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrValueList.value"))
                        )
        );

        return sourceBuilder.toString();
    }


}
