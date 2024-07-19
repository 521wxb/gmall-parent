package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.entity.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonRepository extends ElasticsearchRepository<Person , Long> {

}
