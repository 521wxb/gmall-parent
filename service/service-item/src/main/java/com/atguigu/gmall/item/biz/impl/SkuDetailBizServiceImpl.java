package com.atguigu.gmall.item.biz.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cache.anno.GmallCache;
import com.atguigu.gmall.cache.service.RedisCacheService;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.feign.search.GoodsFeignClient;
import com.atguigu.gmall.item.biz.SkuDetailBizService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.atguigu.gmall.web.vo.CategoryView;
import com.atguigu.gmall.web.vo.SkuDetailVo;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkuDetailBizServiceImpl implements SkuDetailBizService {

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private RedisCacheService redisCacheService ;

    // 创建锁对象
    private static final Lock lock = new ReentrantLock() ;

    @Autowired
    private RedissonClient redissonClient ;         // 注入Redisson客户端对象

    @Autowired
    private GoodsFeignClient goodsFeignClient ;

    // 创建一个bloomFilter
    private static final BloomFilter<Long> bloomFilter = BloomFilter.create(Funnels.longFunnel(), 1000000, 0.000001);

    // @PostConstruct         // 当当前这个bean在这里初始化好了以后，那么此时就会调用该方法
    public void init() {   // 方法的执行时机，是当服务器在启动的时候要执行

        // 调用SkuDetailFeignClient接口方法，获取所有的skuId
        Result<List<Long>> allSkuIds = skuDetailFeignClient.findAllSkuIds();
        List<Long> skuIdsData = allSkuIds.getData();
        skuIdsData.forEach(skuId -> {
            bloomFilter.put(skuId) ;
        });
        log.info("布隆过滤器初始化完毕..............");
        log.info("判断skuId为49的商品的id在布隆过滤器中是否存在:" + bloomFilter.mightContain(49L));
        log.info("判断skuId为100的商品的id在布隆过滤器中是否存在:" + bloomFilter.mightContain(100L));

    }

    // 不能这么来获取bloomFilter
    // private RBloomFilter<Long> redissonBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL);

    private RBloomFilter<Long> redissonBloomFilter ;

    // 获取分布式bloomFilter
    @PostConstruct
    private void getRedissonBloomFilter() {
        log.info("获取了分布式布隆过滤器..............");
        redissonBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL) ;
    }

    /**
     * 思考问题：给redis中要缓存的数据设置过期时间，怎么进行实现？
     * @param skuId
     * @return
     */
    @GmallCache(
            cacheKey = GmallConstant.REDIS_SKU_DETAIL_PREFIX + "#{#params[0]}",
            bloomFilterName = GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL ,
            bloomFilterValue = "#{#params[0]}",
            lockName = GmallConstant.REDIS_SKU_DETAIL_LOCK_PREFIX + "#{#params[0]}",
            enableLock = true
    )
    @Override
    public SkuDetailVo skuDetailBySkuId(Long skuId) {
        return findSkuDetailFromRpc(skuId) ;
    }

    @Override
    public void updateHotScore(Long skuId) {

        // 操作redis计算商品的热度分
        Long hotScore = redisTemplate.opsForValue().increment(GmallConstant.REDSI_SKU_HOTSCORE_PREFIX + skuId);
        log.info("hotScore: {}" , hotScore);
        // 判断热度分是否为100的整数倍
        if(hotScore % 5 == 0) {
            log.info("更新es中商品的热度分, skuId: {} , hotScore: {}" , skuId , hotScore);
            goodsFeignClient.updateHotScore(skuId , hotScore) ;
        }

    }

    /**
     * 分布式布隆过滤器
     * @param skuId
     * @return
     */
    public SkuDetailVo skuDetailBySkuIdRedissonBloomFilter(Long skuId) {

        // 判断当前skuId在布隆过滤器是否存在
        if (!redissonBloomFilter.contains(skuId)) {
            log.error("当前查询的商品在数据库中不存在,bloomFilter也不存在..........");
            return null ;
        }  // 数据库中不存在skuId为100的商品，那么布隆过滤器判断了是存在的(误判)

        // 从Redis中进行数据的查询
        SkuDetailVo detailVo = redisCacheService.getCacheForObject(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
        if(detailVo != null) {
            log.info(Thread.currentThread().getId() + "从redis中查询到了数据，并进行返回..........");
            return detailVo ;
        }

        // 使用Redisson的分布式锁
        RLock clientLock = redissonClient.getLock(GmallConstant.REDIS_SKU_DETAIL_LOCK_PREFIX + skuId);
        boolean isLock = clientLock.tryLock();
        if(isLock) {
            log.info("获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());
            try {

                // 发起远程调用查询数据库
                SkuDetailVo skuDetailVo = findSkuDetailFromRpc(skuId);
                if(skuDetailVo == null) {
                    // 往redis中缓存一个x
                    log.error(Thread.currentThread().getId() + "从数据库中没有查询到数据，在redis中缓存了x， skuId: {}" , skuId );
                    redisCacheService.saveDataToRedis(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    // 把skuDetailVo存储到Redis中
                    log.info(Thread.currentThread().getId() + "从数据库中查询到数据，在redis中缓存了对应的数据， skuId: {}" , skuId );
                    redisCacheService.saveDataToRedis(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , skuDetailVo);
                }

                return skuDetailVo ;

            }catch (Exception e) {
                e.printStackTrace();
                return null ;
            } finally {  // 释放分布式锁
                clientLock.unlock();
                log.info(Thread.currentThread().getId() + "----->释放了分布式锁");
            }

        }else {

            log.info("没有获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());

            // 访问redis查询数据
            detailVo = redisCacheService.getCacheForObject(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
            if(detailVo != null) {
                return detailVo ;
            }else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return redisCacheService.getCacheForObject(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
            }

        }

    }

    public SkuDetailVo skuDetailBySkuIdLocalBloomFilter(Long skuId) {

        // 判断当前skuId在布隆过滤器是否存在
        if (!bloomFilter.mightContain(skuId)) {
            log.error("当前查询的商品在数据库中不存在,bloomFilter也不存在..........");
            return null ;
        }  // 数据库中不存在skuId为100的商品，那么布隆过滤器判断了是存在的(误判)

        // 从Redis中进行数据的查询
        SkuDetailVo detailVo = redisCacheService.getCacheForObject(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
        if(detailVo != null) {
            log.info(Thread.currentThread().getId() + "从redis中查询到了数据，并进行返回..........");
            return detailVo ;
        }

        // 使用Redisson的分布式锁
        RLock clientLock = redissonClient.getLock(GmallConstant.REDIS_SKU_DETAIL_LOCK_PREFIX + skuId);
        boolean isLock = clientLock.tryLock();
        if(isLock) {
            log.info("获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());
            try {

                // 发起远程调用查询数据库
                SkuDetailVo skuDetailVo = findSkuDetailFromRpc(skuId);
                if(skuDetailVo == null) {
                    // 往redis中缓存一个x
                    log.error(Thread.currentThread().getId() + "从数据库中没有查询到数据，在redis中缓存了x， skuId: {}" , skuId );
                    redisCacheService.saveDataToRedis(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    // 把skuDetailVo存储到Redis中
                    log.info(Thread.currentThread().getId() + "从数据库中查询到数据，在redis中缓存了对应的数据， skuId: {}" , skuId );
                    redisCacheService.saveDataToRedis(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , skuDetailVo);
                }

                return skuDetailVo ;

            }catch (Exception e) {
                e.printStackTrace();
                return null ;
            } finally {  // 释放分布式锁
                clientLock.unlock();
                log.info(Thread.currentThread().getId() + "----->释放了分布式锁");
            }

        }else {

            log.info("没有获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());

            // 访问redis查询数据
            detailVo = redisCacheService.getCacheForObject(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
            if(detailVo != null) {
                return detailVo ;
            }else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return redisCacheService.getCacheForObject(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
            }

        }

    }

    /**
     * 使用Redisson框架的分布式锁解决缓存击穿问题
     * @param skuId
     * @return
     */
    public SkuDetailVo skuDetailBySkuIdRedisson(Long skuId) {

        // 判断当前skuId在布隆过滤器是否存在
        if (!bloomFilter.mightContain(skuId)) {
            log.error("当前查询的商品在数据库中不存在,bloomFilter也不存在..........");
            return null ;
        }  // 数据库中不存在skuId为100的商品，那么布隆过滤器判断了是存在的(误判)

        // 从Redis中进行数据的查询
        String skuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId) ;
        if(!StringUtils.isEmpty(skuDetailJson)) {

            // 判断数据是否为x，如果是直接返回null
            if(GmallConstant.REDIS_NULL_VALUE.equals(skuDetailJson)) {
                log.error("从redis中查询到了数据，但是数据的值为x");
                return null;
            }else {
                log.info("从redis中查询到了数据，直接进行返回");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailJson, SkuDetailVo.class);
                return skuDetailVo ;
            }

        }

        // 使用Redisson的分布式锁
        RLock clientLock = redissonClient.getLock(GmallConstant.REDIS_SKU_DETAIL_LOCK_PREFIX + skuId);
        boolean isLock = clientLock.tryLock();
        if(isLock) {
            log.info("获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());
            try {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 发起远程调用查询数据库
                SkuDetailVo skuDetailVo = findSkuDetailFromRpc(skuId);
                if(skuDetailVo == null) {
                    // 往redis中缓存一个x
                    log.error(Thread.currentThread().getId() + "从数据库中没有查询到数据，在redis中缓存了x， skuId: {}" , skuId );
                    redisTemplate.opsForValue().set(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    // 把skuDetailVo存储到Redis中
                    log.info(Thread.currentThread().getId() + "从数据库中查询到数据，在redis中缓存了对应的数据， skuId: {}" , skuId );
                    redisTemplate.opsForValue().set(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
                }

                return skuDetailVo ;

            }catch (Exception e) {
                e.printStackTrace();
                return null ;
            } finally {  // 释放分布式锁
                clientLock.unlock();
                log.info(Thread.currentThread().getId() + "----->释放了分布式锁");
            }

        }else {

            log.info("没有获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());

            // 访问redis
            String redisSkuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId);
            if(!StringUtils.isEmpty(redisSkuDetailJson)) {
                if(GmallConstant.REDIS_NULL_VALUE.equals(redisSkuDetailJson)) {
                    log.error("从redis中查询到了数据，在redis中缓存了x， skuId: {}" , skuId );
                    return null ;
                }else {
                    log.info("从redis中查询到了数据，skuId: {}" , skuId );
                    SkuDetailVo skuDetailVo = JSON.parseObject(redisSkuDetailJson, SkuDetailVo.class);
                    return skuDetailVo ;
                }
            }else {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 查询redis
                redisSkuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId);
                if(GmallConstant.REDIS_NULL_VALUE.equals(redisSkuDetailJson)) {
                    log.error(Thread.currentThread().getId() + "从redis中查询到了数据，在redis中缓存了x， skuId: {}" , skuId );
                    return null ;
                }else {
                    log.info(Thread.currentThread().getId() + "从redis中查询到了数据，skuId: {}" , skuId );
                    SkuDetailVo skuDetailVo = JSON.parseObject(redisSkuDetailJson, SkuDetailVo.class);
                    return skuDetailVo ;
                }

            }

        }

    }

    /**
     * 自定义分布式锁解决缓存击穿问题
     * @param skuId
     * @return
     */
    public SkuDetailVo skuDetailBySkuIdRedis(Long skuId) {

        // 判断当前skuId在布隆过滤器是否存在
        if (!bloomFilter.mightContain(skuId)) {
            log.error("当前查询的商品在数据库中不存在,bloomFilter也不存在..........");
            return null ;
        }  // 数据库中不存在skuId为100的商品，那么布隆过滤器判断了是存在的(误判)

        // 从Redis中进行数据的查询
        String skuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId) ;
        if(!StringUtils.isEmpty(skuDetailJson)) {

            // 判断数据是否为x，如果是直接返回null
            if(GmallConstant.REDIS_NULL_VALUE.equals(skuDetailJson)) {
                log.error("从redis中查询到了数据，但是数据的值为x");
                return null;
            }else {
                log.info("从redis中查询到了数据，直接进行返回");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailJson, SkuDetailVo.class);
                return skuDetailVo ;
            }

        }

        // 使用redis的分布式锁
        String uuid = UUID.randomUUID().toString().replace("-" , "") ;
        Boolean isLock = redisLock(skuId, uuid);
        if(isLock) {
            log.info("获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());
            try {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 发起远程调用查询数据库
                SkuDetailVo skuDetailVo = findSkuDetailFromRpc(skuId);
                if(skuDetailVo == null) {
                    // 往redis中缓存一个x
                    log.error("从数据库中没有查询到数据，在redis中缓存了x， skuId: {}" , skuId );
                    redisTemplate.opsForValue().set(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    // 把skuDetailVo存储到Redis中
                    log.info("从数据库中查询到数据，在redis中缓存了对应的数据， skuId: {}" , skuId );
                    redisTemplate.opsForValue().set(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
                }

                return skuDetailVo ;

            }catch (Exception e) {
                e.printStackTrace();
                return null ;
            } finally {
                redisUnLock(skuId , uuid);
            }

        }else {

            log.info("没有获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());

            // 访问redis
            String redisSkuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId);
            if(!StringUtils.isEmpty(redisSkuDetailJson)) {
                if(GmallConstant.REDIS_NULL_VALUE.equals(redisSkuDetailJson)) {
                    log.error("从redis中查询到了数据，在redis中缓存了x， skuId: {}" , skuId );
                    return null ;
                }else {
                    log.info("从redis中查询到了数据，skuId: {}" , skuId );
                    SkuDetailVo skuDetailVo = JSON.parseObject(redisSkuDetailJson, SkuDetailVo.class);
                    return skuDetailVo ;
                }
            }else {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 查询redis
                redisSkuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId);
                if(GmallConstant.REDIS_NULL_VALUE.equals(redisSkuDetailJson)) {
                    log.error("从redis中查询到了数据，在redis中缓存了x， skuId: {}" , skuId );
                    return null ;
                }else {
                    log.info("从redis中查询到了数据，skuId: {}" , skuId );
                    SkuDetailVo skuDetailVo = JSON.parseObject(redisSkuDetailJson, SkuDetailVo.class);
                    return skuDetailVo ;
                }

            }

        }

    }

    // 解锁方法
    private void redisUnLock(Long skuId , String uuid) {

        // 定义lua脚本
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;

        // 执行lua脚本
        /**
         * RedisScript<T> script: 封装了lua脚本的一个对象
         * List<K> keys：操作的key的集合，这个集合中的元素的索引是从1开始
         * Object... args：表示的就是执行lua脚本的参数，参数的索引是从1开始
         */
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(GmallConstant.REDIS_SKU_DETAIL_LOCK_PREFIX + skuId), uuid);
        if(result == 1) {
            log.info("删除了自己的锁对象....");
        }else  {
            log.info("锁是别人的，删除失败.........");
        }

    }

    // 定义加锁的方法
    private Boolean redisLock(Long skuId , String uuid) {   // 返回true表示获取到了锁，返回false表示没有获取到锁
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(GmallConstant.REDIS_SKU_DETAIL_LOCK_PREFIX + skuId,
                        uuid, GmallConstant.REDIS_SKU_DETAIL_LOCK_TIMEOUT, TimeUnit.SECONDS);
        return result ;
    }

    // 使用本地锁解决缓存击穿问题
    public SkuDetailVo skuDetailBySkuIdLocalLock(Long skuId) {

        // 判断当前skuId在布隆过滤器是否存在
        if (!bloomFilter.mightContain(skuId)) {
            log.error("当前查询的商品在数据库中不存在,bloomFilter也不存在..........");
            return null ;
        }  // 数据库中不存在skuId为100的商品，那么布隆过滤器判断了是存在的(误判)

        // 从Redis中进行数据的查询
        String skuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId) ;
        if(!StringUtils.isEmpty(skuDetailJson)) {

            // 判断数据是否为x，如果是直接返回null
            if(GmallConstant.REDIS_NULL_VALUE.equals(skuDetailJson)) {
                log.error("从redis中查询到了数据，但是数据的值为x");
                return null;
            }else {
                log.info("从redis中查询到了数据，直接进行返回");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailJson, SkuDetailVo.class);
                return skuDetailVo ;
            }

        }

        // 加锁防止缓存击穿
        // lock.lock();                             // 获取锁，如果没有获取到就一直等待
        boolean isLock = lock.tryLock() ;            // 尝试的获取锁，如果获取到锁了，那么此时返回的true。如果没有获取到锁返回的是false！
        if(isLock) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 发起远程调用查询数据库
            SkuDetailVo skuDetailVo = findSkuDetailFromRpc(skuId);
            if(skuDetailVo == null) {
                // 往redis中缓存一个x
                log.error("从数据库中没有查询到数据，在redis中缓存了x， skuId: {}" , skuId );
                redisTemplate.opsForValue().set(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
            }else {
                // 把skuDetailVo存储到Redis中
                log.info("从数据库中查询到数据，在redis中缓存了对应的数据， skuId: {}" , skuId );
                redisTemplate.opsForValue().set(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
            }

            // 考虑本地锁的释放问题

            return skuDetailVo ;

        }else {

            // 访问redis
            String redisSkuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId);
            if(!StringUtils.isEmpty(redisSkuDetailJson)) {
                if(GmallConstant.REDIS_NULL_VALUE.equals(redisSkuDetailJson)) {
                    return null ;
                }else {
                    SkuDetailVo skuDetailVo = JSON.parseObject(redisSkuDetailJson, SkuDetailVo.class);
                    return skuDetailVo ;
                }
            }else {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 查询redis
                redisSkuDetailJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId);
                if(GmallConstant.REDIS_NULL_VALUE.equals(redisSkuDetailJson)) {
                    return null ;
                }else {
                    SkuDetailVo skuDetailVo = JSON.parseObject(redisSkuDetailJson, SkuDetailVo.class);
                    return skuDetailVo ;
                }

            }

        }

    }

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    // 发起远程调用查询数据库
    private SkuDetailVo findSkuDetailFromRpc(Long skuId) {

        // 创建SkuDetailVo对象封装数据
        SkuDetailVo skuDetailVo = new SkuDetailVo() ;

        // 调用product微服务的根据skuId查询skuInfo数据以及图片数据
        Result<SkuInfo> skuInfoResult = skuDetailFeignClient.findBySkuInfoAndSkuImagesBySkuId(skuId);
        SkuInfo skuInfo = skuInfoResult.getData();
        if(skuInfo == null) {
            return null ;
        }
        skuDetailVo.setSkuInfo(skuInfo);

        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {

            // 根据skuId的查询其所对应的三级分类的数据
            Result<CategoryView> categoryViewResult = skuDetailFeignClient.findByCategoryBySkuId(skuId);
            CategoryView categoryView = categoryViewResult.getData();
            skuDetailVo.setCategoryView(categoryView);
            log.info(Thread.currentThread().getName() + "----->根据skuId的查询其所对应的三级分类的数据");  // 打印日志

        }, threadPoolExecutor);


        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {

            // 远程调用product微服务的接口查询价格数据
            Result<SkuInfo> infoResult = skuDetailFeignClient.findSkuInfoBySkuId(skuId);
            SkuInfo infoResultData = infoResult.getData();
            skuDetailVo.setPrice(infoResultData.getPrice());
            log.info(Thread.currentThread().getName() + "----->远程调用product微服务的接口查询价格数据");      // 打印日志

        }, threadPoolExecutor);


        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> {

            // 远程调用product微服务的接口查询spu的销售属性和销售属性的值
            Result<List<SpuSaleAttr>> spuSalAttrResult = skuDetailFeignClient.findSpuSalAttrBySkuId(skuId);
            List<SpuSaleAttr> spuSalAttrResultData = spuSalAttrResult.getData();
            skuDetailVo.setSpuSaleAttrList(spuSalAttrResultData);
            log.info(Thread.currentThread().getName() + "----->远程调用product微服务的接口查询spu的销售属性和销售属性的值"); // 打印日志

        }, threadPoolExecutor);


        CompletableFuture<Void> cf4 = CompletableFuture.runAsync(() -> {

            // 远程调用product微服务，根据skuId获取所有的兄弟sku的销售属性值的组合
            Result<List<AttrValueConcatVo>> brotherSkuSaleAttrValueConcatBySkuId = skuDetailFeignClient.findBrotherSkuSaleAttrValueConcatBySkuId(skuId);
            List<AttrValueConcatVo> valueConcatVoList = brotherSkuSaleAttrValueConcatBySkuId.getData();

            // Collectors.toMap将流中的元素转换成Map，方法的第一个参数是用来构建Map的键，方法的第二个参数是用来构建Map的值
            Map<String, Long> map = valueConcatVoList.stream().
                    collect(Collectors.toMap(attrValueConcatVo -> attrValueConcatVo.getAttrValueConcat(), attrValueConcatVo -> attrValueConcatVo.getSkuId()));
            String valuesSkuJson = JSON.toJSONString(map);
            skuDetailVo.setValuesSkuJson(valuesSkuJson);
            log.info(Thread.currentThread().getName() + "----->远程调用product微服务，根据skuId获取所有的兄弟sku的销售属性值的组合"); // 打印日志


        }, threadPoolExecutor);

        // 需要让4个异步任务执行完毕以后，在进行返回
        CompletableFuture.allOf(cf1 , cf2 , cf3 , cf4).join() ;

        // 返回
        return skuDetailVo ;

    }

    /**
     * 使用线程池和CountDownLatch完成异步远程调用代码优化
     * @param skuId
     * @return
     */
    private SkuDetailVo findSkuDetailFromRpcThreadPoolExecutor(Long skuId) {

        // 创建SkuDetailVo对象封装数据
        SkuDetailVo skuDetailVo = new SkuDetailVo() ;

        // 调用product微服务的根据skuId查询skuInfo数据以及图片数据
        Result<SkuInfo> skuInfoResult = skuDetailFeignClient.findBySkuInfoAndSkuImagesBySkuId(skuId);
        SkuInfo skuInfo = skuInfoResult.getData();
        if(skuInfo == null) {
            return null ;
        }
        skuDetailVo.setSkuInfo(skuInfo);

        // 需要使用CountDownLatch来进行改造，等到其他的四个线程执行完毕以后，在执行当前线程
        CountDownLatch countDownLatch = new CountDownLatch(4) ;

        threadPoolExecutor.submit(() -> {

            // 根据skuId的查询其所对应的三级分类的数据
            Result<CategoryView> categoryViewResult = skuDetailFeignClient.findByCategoryBySkuId(skuId);
            CategoryView categoryView = categoryViewResult.getData();
            skuDetailVo.setCategoryView(categoryView);

            // 打印日志
            log.info(Thread.currentThread().getName() + "----->根据skuId的查询其所对应的三级分类的数据" );
            countDownLatch.countDown();   // 让CountDownLatch中的计数器进行-1

        }) ;


        threadPoolExecutor.submit(() -> {

            // 远程调用product微服务的接口查询价格数据
            Result<SkuInfo> infoResult = skuDetailFeignClient.findSkuInfoBySkuId(skuId);
            SkuInfo infoResultData = infoResult.getData();
            skuDetailVo.setPrice(infoResultData.getPrice());

            // 打印日志
            log.info(Thread.currentThread().getName() + "----->远程调用product微服务的接口查询价格数据" );
            countDownLatch.countDown();   // 让CountDownLatch中的计数器进行-1

        }) ;

        threadPoolExecutor.submit(() -> {

            // 远程调用product微服务的接口查询spu的销售属性和销售属性的值
            Result<List<SpuSaleAttr>> spuSalAttrResult = skuDetailFeignClient.findSpuSalAttrBySkuId(skuId);
            List<SpuSaleAttr> spuSalAttrResultData = spuSalAttrResult.getData();
            skuDetailVo.setSpuSaleAttrList(spuSalAttrResultData);

            // 打印日志
            log.info(Thread.currentThread().getName() + "----->远程调用product微服务的接口查询spu的销售属性和销售属性的值" );
            countDownLatch.countDown();   // 让CountDownLatch中的计数器进行-1

        }) ;

        threadPoolExecutor.submit(() -> {

            // 远程调用product微服务，根据skuId获取所有的兄弟sku的销售属性值的组合
            Result<List<AttrValueConcatVo>> brotherSkuSaleAttrValueConcatBySkuId = skuDetailFeignClient.findBrotherSkuSaleAttrValueConcatBySkuId(skuId);
            List<AttrValueConcatVo> valueConcatVoList = brotherSkuSaleAttrValueConcatBySkuId.getData();

            // Collectors.toMap将流中的元素转换成Map，方法的第一个参数是用来构建Map的键，方法的第二个参数是用来构建Map的值
            Map<String, Long> map = valueConcatVoList.stream().
                    collect(Collectors.toMap(attrValueConcatVo -> attrValueConcatVo.getAttrValueConcat(), attrValueConcatVo -> attrValueConcatVo.getSkuId()));
            String valuesSkuJson = JSON.toJSONString(map);
            skuDetailVo.setValuesSkuJson(valuesSkuJson);

            // 打印日志
            log.info(Thread.currentThread().getName() + "----->远程调用product微服务，根据skuId获取所有的兄弟sku的销售属性值的组合" );
            countDownLatch.countDown();   // 让CountDownLatch中的计数器进行-1

        }) ;

        try {
            countDownLatch.await();         // 当执行当前方法的线程阻塞，等到其他的线程执行完毕以后在执行当前线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 返回
        return skuDetailVo ;

    }


}
