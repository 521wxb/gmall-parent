package com.atguigu.gmall.search.biz.impl;
import cn.hutool.core.util.PageUtil;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.atguigu.gmall.search.vo.SearchRespAttrVo;
import com.atguigu.gmall.search.vo.SearchTmVo;
import com.google.common.collect.Lists;
import com.atguigu.gmall.search.vo.SearchOrderMapVo;

import com.atguigu.gmall.search.biz.GoodsBizService;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class GoodsBizServiceImpl implements GoodsBizService {

    @Autowired
    private ElasticsearchRestTemplate esRestTemplate ;

    @Autowired
    private GoodsRepository goodsRepository ;

    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods) ;
    }

    @Override
    public void deleteGoodsById(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 高级搜索
     * @param searchParamDTO
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamDTO searchParamDTO) {

        log.info("GoodsBizServiceImpl...search...方法执行了......");

        // 创建QueryBuilder，封装搜索方式
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 三级分类的搜索条件
        if(searchParamDTO.getCategory1Id() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category1Id" , searchParamDTO.getCategory1Id())) ;
        }
        if(searchParamDTO.getCategory2Id() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category2Id" , searchParamDTO.getCategory2Id())) ;
        }
        if(searchParamDTO.getCategory3Id() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category3Id" , searchParamDTO.getCategory3Id())) ;
        }

        // 构建搜索关键字的搜索条件
        if(!StringUtils.isEmpty(searchParamDTO.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title" , searchParamDTO.getKeyword())) ;
        }else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery()) ;
        }

        // 构建品牌的搜索条件
        if(!StringUtils.isEmpty(searchParamDTO.getTrademark())) {
            String[] trademarkArray = searchParamDTO.getTrademark().split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId" , Long.parseLong(trademarkArray[0]))) ;
        }

        // 构建平台属性的搜索条件
        String[] props = searchParamDTO.getProps();
        if(props != null && props.length > 0) {
            for(String prop : props) {
                String[] propArray = prop.split(":");
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId" , propArray[0])) ;
                boolQuery.must(QueryBuilders.termQuery("attrs.attrValue" , propArray[1])) ;
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.must(nestedQuery) ;
            }
        }

        // 创建NativeSearchQuery对象
        PageRequest pageRequest = PageRequest.of(searchParamDTO.getPageNo() - 1, searchParamDTO.getPageSize());
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build() ;

        // 给nativeSearchQuery设置品牌聚合的参数
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId").size(100);
        aggregationBuilder.subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName").size(1)) ;
        aggregationBuilder.subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl").size(1)) ;
        nativeSearchQuery.addAggregation(aggregationBuilder);

        // 给nativeSearchQuery设置平台属性聚合的参数
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(200);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1)) ;
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100)) ;
        nestedAggregationBuilder.subAggregation(attrIdAgg) ;
        nativeSearchQuery.addAggregation(nestedAggregationBuilder);

        // 设置高亮的相关参数
        if(!StringUtils.isEmpty(searchParamDTO.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder() ;
            highlightBuilder.field("title") ;
            highlightBuilder.preTags("<font color='red'>") ;
            highlightBuilder.postTags("</font>") ;
            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder) ;
            nativeSearchQuery.setHighlightQuery(highlightQuery);
        }

        // 构建排序条件
        String order = searchParamDTO.getOrder();
        if(!StringUtils.isEmpty(order)) {
            Sort sort = null ;
            String[] sortArray = order.split(":");
            switch (sortArray[0]) {
                case "1":
                    sort = Sort.by("asc".equalsIgnoreCase(sortArray[1]) ? Sort.Direction.ASC : Sort.Direction.DESC , "hotScore") ;
                    break;
                case "2":
                    sort = Sort.by("asc".equalsIgnoreCase(sortArray[1]) ? Sort.Direction.ASC : Sort.Direction.DESC , "price") ;
                    break;
            }
            nativeSearchQuery.addSort(sort) ;
        }

        // 进行搜索
        SearchHits<Goods> goodsSearchHits = esRestTemplate.search(nativeSearchQuery, Goods.class);
        SearchResponseVo searchResponseVo = parseReponseResult(goodsSearchHits , searchParamDTO) ;

        // 返回
        return searchResponseVo;
    }

    @Override
    public void updateHotScore(Long skuId, Long hotScore) {
        Optional<Goods> optional = goodsRepository.findById(skuId);
        Goods goods = optional.get();
        goods.setHotScore(hotScore);
        goodsRepository.save(goods) ;
    }

    /**
     * 解析响应结果数据，然后返回SearchResponseVo
     * @param goodsSearchHits
     * @param searchParamDTO
     * @return
     */
    private SearchResponseVo parseReponseResult(SearchHits<Goods> goodsSearchHits, SearchParamDTO searchParamDTO) {

        // 创建SearchResponseVo对象
        SearchResponseVo searchResponseVo = new SearchResponseVo() ;

        // 封装请求参数对象相关数据
        searchResponseVo.setSearchParam(searchParamDTO);

        // 封装品牌面包屑的数据
        String trademark = searchParamDTO.getTrademark();
        if(!StringUtils.isEmpty(trademark)) {
            String[] trademarkArray = trademark.split(":");
            searchResponseVo.setTrademarkParam("品牌:" + trademarkArray[1]);
        }

        // 封装平台属性的面包屑数据
        String[] props = searchParamDTO.getProps();
        if(props != null && props.length > 0) {
            List<SearchAttr> searchAttrList = new ArrayList<>() ;
            for(String prop : props) {
                String[] attrArray = prop.split(":");
                Long attrId = Long.parseLong(attrArray[0]) ;
                String attrValue = attrArray[1] ;
                String attrName = attrArray[2] ;
                SearchAttr searchAttr = new SearchAttr() ;
                searchAttr.setAttrId(attrId);
                searchAttr.setAttrValue(attrValue);
                searchAttr.setAttrName(attrName);
                searchAttrList.add(searchAttr) ;
            }
            searchResponseVo.setPropsParamList(searchAttrList);
        }

        // 品牌列表封装
        Aggregations aggregations = goodsSearchHits.getAggregations();
        ParsedLongTerms parsedLongTerms = aggregations.get("tmIdAgg") ;
        List<? extends Terms.Bucket> buckets = parsedLongTerms.getBuckets();
        List<SearchTmVo> searchTmVoList = new ArrayList<>() ;
        for(Terms.Bucket bucket : buckets) {

            // 品牌的id
            Long tmId = Long.parseLong(bucket.getKeyAsString()) ;

            // 获取品牌名称的子聚合结果
            Aggregations bucketAggregations = bucket.getAggregations();
            ParsedStringTerms tmNameParsedStringTerms = bucketAggregations.get("tmNameAgg") ;
            String tmName = tmNameParsedStringTerms.getBuckets().get(0).getKeyAsString() ;

            // 获取品牌的logoUrl
            ParsedStringTerms tmLogoUrlAggArsedStringTerms = bucketAggregations.get("tmLogoUrlAgg") ;
            String tmLogoUrl = tmLogoUrlAggArsedStringTerms.getBuckets().get(0).getKeyAsString();

            // 封装数据到SearchTmVo
            SearchTmVo searchTmVo = new SearchTmVo() ;
            searchTmVo.setTmId(tmId);
            searchTmVo.setTmName(tmName);
            searchTmVo.setTmLogoUrl(tmLogoUrl);
            searchTmVoList.add(searchTmVo) ;

        }
        searchResponseVo.setTrademarkList(searchTmVoList);

        // 封装平台属性
        ParsedNested attrAgg = aggregations.get("attrAgg");
        Aggregations aggAggregations = attrAgg.getAggregations();
        ParsedLongTerms attrIdAggAggregation = aggAggregations.get("attrIdAgg");
        List<? extends Terms.Bucket> idAggAggregationBuckets = attrIdAggAggregation.getBuckets();
        List<SearchRespAttrVo> searchRespAttrVos = new ArrayList<>() ;  // 所有的平台属性和平台属性值
        for(Terms.Bucket bucket : idAggAggregationBuckets) {

            // 平台属性的id
            Long attrId = Long.parseLong(bucket.getKeyAsString());

            // 获取平台属性的名称
            ParsedStringTerms attrNameAgge = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgge.getBuckets().get(0).getKeyAsString() ;

            // 获取平台属性的值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<? extends Terms.Bucket> attrValueAggBuckets = attrValueAgg.getBuckets();
            List<String> attrValueList = new ArrayList<>() ;   // 平台属性的值的集合
            for(Terms.Bucket buc : attrValueAggBuckets) {
                String attrValue = buc.getKeyAsString() ;
                attrValueList.add(attrValue) ;
            }

            // 封装数据
            SearchRespAttrVo searchRespAttrVo = new SearchRespAttrVo() ;
            searchRespAttrVo.setAttrId(attrId);
            searchRespAttrVo.setAttrName(attrName);
            searchRespAttrVo.setAttrValueList(attrValueList);
            searchRespAttrVos.add(searchRespAttrVo) ;

        }
        searchResponseVo.setAttrsList(searchRespAttrVos);

        // 封装排序数据
        String dtoOrder = searchParamDTO.getOrder();
        SearchOrderMapVo searchOrderMapVo = new SearchOrderMapVo("1" , "asc") ;
        if(!StringUtils.isEmpty(dtoOrder) && dtoOrder.contains(":")) {
            String[] orderArray = dtoOrder.split(":");
            searchOrderMapVo.setType(orderArray[0]);
            searchOrderMapVo.setSort(orderArray[1]);
        }
        searchResponseVo.setOrderMap(searchOrderMapVo);

        // 封装当前页数据
        List<Goods> goodsList = new ArrayList<>() ;
        List<SearchHit<Goods>> searchHits = goodsSearchHits.getSearchHits();
        for(SearchHit<Goods> searchHit : searchHits) {
            Goods goods = searchHit.getContent();

            // 获取高亮结果
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            if(highlightFields != null && highlightFields.size() > 0) {
                List<String> list = highlightFields.get("title");
                if(list != null && list.size() > 0) {
                    String highlightTitle = list.get(0) ;
                    goods.setTitle(highlightTitle);
                }
            }

            goodsList.add(goods) ;
        }
        searchResponseVo.setGoodsList(goodsList);

        // 封装分页相关的数据
        searchResponseVo.setPageNo(searchParamDTO.getPageNo());

        // 获取到满足条件的总记录数
        Long count = goodsSearchHits.getTotalHits();
        int totalPage = PageUtil.totalPage(count.intValue(), searchParamDTO.getPageSize());
        searchResponseVo.setTotalPages(totalPage);

        // 构建urlParm参数
        String urlParam = getUrlParam(searchParamDTO) ;
        searchResponseVo.setUrlParam(urlParam);

        // 返回
        return searchResponseVo ;

    }

    /**
     * 根据请求参数构建urlParam的参数
     * @param searchParamDTO
     * @return
     */
    private String getUrlParam(SearchParamDTO searchParamDTO) {

        // 创建StringBuilder对象
        StringBuilder sb = new StringBuilder("list.html?") ;

        // 拼接三级分类的请求参数
        if(searchParamDTO.getCategory1Id() != null) {
            sb.append("&category1Id=" + searchParamDTO.getCategory1Id()) ;
        }

        if(searchParamDTO.getCategory2Id() != null) {
            sb.append("&category2Id=" + searchParamDTO.getCategory2Id()) ;
        }

        if(searchParamDTO.getCategory3Id() != null) {
            sb.append("&category3Id=" + searchParamDTO.getCategory3Id()) ;
        }

        // 拼接keyword
        String keyword = searchParamDTO.getKeyword();
        if(!StringUtils.isEmpty(keyword)) {
            sb.append("&keyword=" + keyword) ;
        }

        // 拼接品牌的搜索条件
        String trademark = searchParamDTO.getTrademark();
        if(!StringUtils.isEmpty(trademark)) {
            sb.append("&trademark=" + trademark) ;
        }

        // 拼接平台属性搜索条件
        String[] props = searchParamDTO.getProps();
        if(props != null && props.length > 0) {
            for(String prop: props) {
                sb.append("&props="  + prop) ;
            }
        }

        // 返回
        return sb.toString() ;

    }

}
