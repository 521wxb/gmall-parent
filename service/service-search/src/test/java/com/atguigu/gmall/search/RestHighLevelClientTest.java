package com.atguigu.gmall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.entity.Person;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(classes = SearchApplication.class)
public class RestHighLevelClientTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient ;

    /**
     * 高级搜索
     * @throws
     */
    @Test
    public void search() throws IOException {

        SearchRequest searchRequest = new SearchRequest("person") ;
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder() ;
        sourceBuilder.query(QueryBuilders.matchAllQuery()) ;
        searchRequest.source(sourceBuilder) ;

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        long count = searchHits.getTotalHits().value ;
        System.out.println(count);

        SearchHit[] searchHitsHits = searchHits.getHits();
        for(SearchHit searchHit : searchHitsHits) {
            String sourceAsString = searchHit.getSourceAsString();
            System.out.println(sourceAsString);
        }

    }

    @Test
    public void updateDocument() throws IOException {

        // 准备数据
        Person p = new Person() ;
        p.setId(1L);
        p.setUsername("尚硅谷教育2");
        p.setAddress("西安市高新区科技5路2");
        p.setAge(1);
        String dataJson = JSON.toJSONString(p);

        UpdateRequest updateRequest = new UpdateRequest("person" , "1") ;
        updateRequest.doc(dataJson , XContentType.JSON) ;
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(updateResponse);
    }

    @Test
    public void getDocuemt() throws IOException {
        GetRequest getRequest = new GetRequest("person").id("1") ;
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = response.getSourceAsString();
        System.out.println(sourceAsString);
    }

    @Test
    public void saveDocument() throws IOException {

        // 创建添加文档的请求对象
        IndexRequest indexRequest = new IndexRequest("person").id("1") ;

        // 准备数据
        Person p = new Person() ;
        p.setId(1L);
        p.setUsername("尚硅谷教育");
        p.setAddress("西安市高新区科技5路");
        p.setAge(2);
        String dataJson = JSON.toJSONString(p);

        // 把数据设置给IndexRequest对象
        indexRequest.source(dataJson , XContentType.JSON) ;

        // 发送请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);

    }
}
