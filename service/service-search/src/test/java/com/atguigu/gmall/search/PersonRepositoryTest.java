package com.atguigu.gmall.search;

import com.atguigu.gmall.search.entity.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * ElasticsearchRestTemplate: 进行复杂的搜索
 * ElasticsearchRepository： 完成基础的CURD的
 */
@SpringBootTest(classes = SearchApplication.class)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository ;

    @Test
    public void delete() {
        personRepository.deleteById(1L);
    }

    @Test
    public void updateById() {

        //  准备数据
        Person p = new Person() ;
        p.setId(2L);
        p.setUsername("尚硅谷教育3");
        p.setAddress("西安市高新区科技5路3");
        p.setAge(3);

        personRepository.save(p) ;
    }


    @Test
    public void findById() {
        Optional<Person> optional = personRepository.findById(1L);
        Person person = optional.get();
        System.out.println(person);
    }

    @Test
    public void save() {

        //  准备数据
        Person p = new Person() ;
        p.setId(2L);
        p.setUsername("尚硅谷教育2");
        p.setAddress("西安市高新区科技5路2");
        p.setAge(2);

        personRepository.save(p) ;
    }

}


