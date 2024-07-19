package com.atguigu.gmall.product.biz.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.web.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CategoryBizServiceImpl implements CategoryBizService  {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper ;

    // 创建一个Map，使用Map来存储查询到的三级分类数据
    private static final ConcurrentHashMap<String , String> cacheMap = new ConcurrentHashMap<>() ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    /**
     * 解决缓存穿透的问题，将数据库不存在的数据也加入到Redis中
     * @return
     */
    @Override
    public List<CategoryVo> findAllCategory() {

        // 从redis中查询数据
        String categoryListJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_KEY_CATEGORY);
        if(!StringUtils.isEmpty(categoryListJson)) {

            if(GmallConstant.REDIS_NULL_VALUE.equals(categoryListJson)) {
                log.info("从缓存中查询到了的数据,但是这个数据是x，在数据库中根本就不存在........");
                return null ;
            }

            log.info("从缓存中查询到了的数据........");
            List<CategoryVo> categoryVoList = JSON.parseArray(categoryListJson, CategoryVo.class);
            return categoryVoList ;
        }

        // 查询数据库
        List<CategoryVo> categoryVoList = baseCategory1Mapper.findAllCategory();
        if(categoryVoList != null && categoryVoList.size() > 0) {
            log.info("从数据库查询到了的数据........");
            redisTemplate.opsForValue().set(GmallConstant.REDIS_KEY_CATEGORY , JSON.toJSONString(categoryVoList));  // 把数据存储到Redis中
        }else  {
            log.info("从数据库查询,但是数据库中没有数据，此时向Redis存储了X........");
            redisTemplate.opsForValue().set(GmallConstant.REDIS_KEY_CATEGORY , GmallConstant.REDIS_NULL_VALUE);
        }

        // 返回
        return categoryVoList ;
    }

    /**
     * 应该使用Redis中的哪一种数据类型来存储数据?
     *
     * string
     * list
     * set
     * hash
     * zset
     *
     * RedisTemplate的思想：先调用redisTemplate中的opsForXXX的方法获取到操作指定数据类型的操作对象
     * 然后在调用操作对象的方法完成数据的操作
     *
     */
    public List<CategoryVo> findAllCategoryRedis() {

        // 从redis中查询数据
        String categoryListJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_KEY_CATEGORY);
        if(!StringUtils.isEmpty(categoryListJson)) {
            log.info("从缓存中查询到了的数据........");
            List<CategoryVo> categoryVoList = JSON.parseArray(categoryListJson, CategoryVo.class);
            return categoryVoList ;
        }

        // 查询数据库
        List<CategoryVo> categoryVoList = baseCategory1Mapper.findAllCategory();
        log.info("从数据库查询到了的数据........");

        // 把数据存储到Redis中
        redisTemplate.opsForValue().set(GmallConstant.REDIS_KEY_CATEGORY , JSON.toJSONString(categoryVoList));

        return categoryVoList ;
    }

    /**
     * HashMap：不是线程安全的
     * Hashtable：是可以保证线程安全性的 ， Hashtable保证线程安全性效率较低 ， 原因是因为使用的全局锁(synchronized)
     * @return
     */
    public List<CategoryVo> findAllCategoryConcurrentHashMap() {

        // 从cacheMap中查询数据
        String categoryListJson = cacheMap.get("categoryList");
        if(!StringUtils.isEmpty(categoryListJson)) {
            log.info("从缓存中查询到了的数据........");
            List<CategoryVo> categoryVoList = JSON.parseArray(categoryListJson, CategoryVo.class);
            return categoryVoList ;
        }

        // 查询数据库
        List<CategoryVo> categoryVoList = baseCategory1Mapper.findAllCategory();
        log.info("从数据库查询到了的数据........");

        // 把数据存储到缓存中
        cacheMap.put("categoryList" , JSON.toJSONString(categoryVoList)) ;

        return categoryVoList ;
    }
}
