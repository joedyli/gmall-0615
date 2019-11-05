package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.vo.SearchParamVO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Service
public class SearchService {

    @Autowired
    private JestClient jestClient;

    public void search(SearchParamVO searchParamVO) {

        try {
            String dsl = buildDSL(searchParamVO);
            System.out.println(dsl);
            Search search = new Search.Builder(dsl).addIndex("goods").addType("info").build();

            SearchResult searchResult = this.jestClient.execute(search);
            System.out.println(searchResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
