package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.BloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BloomFilterServiceImpl implements BloomFilterService  {

    @Autowired
    private SkuInfoMapper skuInfoMapper ;

    @Autowired
    private RedissonClient redissonClient ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public void resetBloomFilter() {

        // 查询所有的skuIds
        List<Long> allSkuIds = skuInfoMapper.findAllSkuIds();

        // 创建新的bloomFilter
        RBloomFilter<Long> newBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL_NEW);
        newBloomFilter.tryInit(1000000 , 0.000001) ;
        allSkuIds.forEach(skuId -> newBloomFilter.add(skuId) );
        newBloomFilter.add(100L) ;
        log.info("新的bloomFilter创建好了, 判断100在bloomFilter中是否存在:" + newBloomFilter.contains(100L));

        // 删除原先的bloomFilter，删除原先的bloomFilter的配置。把新创建的布隆过滤器重命名为之前bloomFilter的名称，新创建的bloomFilter配置信息也需要重命名
        String script = "redis.call(\"del\" , KEYS[1])\n" +
                "redis.call(\"del\" , \"{\"..KEYS[1]..\"}:config\")\n" +
                "redis.call(\"rename\" , KEYS[2] , KEYS[1])\n" +
                "redis.call(\"rename\" , \"{\"..KEYS[2]..\"}:config\" , \"{\"..KEYS[1]..\"}:config\")\n" +
                "return 1" ;

        redisTemplate.execute(new DefaultRedisScript<>(script , Long.class) ,
                Arrays.asList(GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL , GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL_NEW)) ;
        log.info("BloomFilter已经重置完毕....");

    }

}
