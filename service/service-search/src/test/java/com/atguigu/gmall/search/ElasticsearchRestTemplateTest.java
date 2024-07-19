package com.atguigu.gmall.search;

import com.atguigu.gmall.search.entity.Person;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.util.List;

@SpringBootTest(classes = SearchApplication.class)
public class ElasticsearchRestTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate ;

    /**
     * 实现高级搜索
     */
    @Test
    public void search() {

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(PageRequest.of(0 , 2))
                .build();
        SearchHits<Person> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Person.class, IndexCoordinates.of("person"));
        List<SearchHit<Person>> searchHitList = searchHits.getSearchHits();
        searchHitList.forEach(searchHit -> {
            Person content = searchHit.getContent();
            System.out.println(content);
        });

    }


    @Test
    public void updateDocument() {
        Document document = Document.create();
        document.put("username" , "尚硅谷IT教育") ;
        UpdateQuery updateQuery = UpdateQuery.builder("1")
                .withDocument(document)
                .build() ;
        elasticsearchRestTemplate.update(updateQuery , IndexCoordinates.of("person")) ;

    }

    @Test
    public void getDoument() {
        //  保存数据
        Person person = elasticsearchRestTemplate.get("1", Person.class, IndexCoordinates.of("person"));
        System.out.println(person);
    }

    @Test
    public void addDocument() {

        //  准备数据
        Person p = new Person() ;
        p.setId(1L);
        p.setUsername("尚硅谷教育");
        p.setAddress("西安市高新区科技5路");
        p.setAge(2);

        //  保存数据
        elasticsearchRestTemplate.save(p) ;
    }

}
