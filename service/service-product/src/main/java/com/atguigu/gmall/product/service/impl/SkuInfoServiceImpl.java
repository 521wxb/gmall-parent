package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.feign.search.GoodsFeignClient;
import com.atguigu.gmall.product.entity.*;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.atguigu.gmall.web.vo.CategoryView;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.product.dto.SkuInfoDTO;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.search.entity.Goods;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Slf4j
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService{

    @Autowired
    private SkuImageService skuImageService ;

    @Autowired
    private SkuAttrValueService skuAttrValueService ;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService ;

    @Autowired
    private SkuInfoMapper skuInfoMapper ;

    @Autowired
    private GoodsFeignClient goodsFeignClient ;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper ;

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper ;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper ;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    @Override
    public Page findByPage(Integer pageNo, Integer pageSize) {
        Page page = new Page(pageNo , pageSize) ;
        page(page) ;   // 调用的是IService接口中的page方法进行分页查询
        return page;
    }

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfoDTO skuInfoDTO) {

        // skuInfo的数据
        SkuInfo skuInfo = new SkuInfo() ;
        BeanUtils.copyProperties(skuInfoDTO , skuInfo);
        skuInfo.setIsSale(0);
        save(skuInfo) ;

        // 获取skuId的id
        Long skuId = skuInfo.getId();

        // sku的图片数据
        List<SkuImage> skuImageList = skuInfoDTO.getSkuImageList();
        skuImageList = skuImageList.stream().map(skuImage -> {
            skuImage.setSkuId(skuId);
            return skuImage;
        }).collect(Collectors.toList());
        skuImageService.saveBatch(skuImageList) ;

        // sku的平台属性的值
        List<SkuAttrValue> skuAttrValueList = skuInfoDTO.getSkuAttrValueList();
        skuAttrValueList = skuAttrValueList.stream().map(skuAttrValue -> {
            skuAttrValue.setSkuId(skuId);
            return skuAttrValue ;
        }).collect(Collectors.toList()) ;
        skuAttrValueService.saveBatch(skuAttrValueList) ;

        // sku的销售属性的值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfoDTO.getSkuSaleAttrValueList();
        skuSaleAttrValueList = skuSaleAttrValueList.stream().map(skuSaleAttrValue -> {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfoDTO.getSpuId().intValue());
            return skuSaleAttrValue ;
        }).collect(Collectors.toList()) ;
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList) ;

    }

    @Override
    public void onSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo() ;
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        updateById(skuInfo) ;

        // 调用service-search微服务的保存数据的接口
        Goods goods = buildGoods(skuId) ;
        goodsFeignClient.saveGoods(goods) ;

    }

    // 构建Goods对象的方法
    private Goods buildGoods(Long skuId) {

        // 创建Goods对象，封装相关的数据
        Goods goods = new Goods();

        // 查询数据库封装相关数据
        // 查询Sku的基本数据
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
            goods.setId(skuInfo.getId());
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setTitle(skuInfo.getSkuName());
            goods.setPrice(new BigDecimal(skuInfo.getPrice()));
            goods.setCreateTime(new Date());
        } , threadPoolExecutor);

        // 根据品牌的id查询品牌数据
        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {
            BaseTrademark baseTrademark = baseTrademarkMapper.selectById(skuInfo.getTmId());
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        }, threadPoolExecutor);

        // 根据skuId查询三级分类的数据
        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> {
            CategoryView categoryView = baseCategory1Mapper.findByCategoryBySkuId(skuId);
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Id(categoryView.getCategory3Id());
            goods.setCategory3Name(categoryView.getCategory3Name());
        }, threadPoolExecutor);

        // 热度分设置为0
        goods.setHotScore(0L);

        // sku的平台属性和平台属性的值
        CompletableFuture<Void> cf4 = CompletableFuture.runAsync(() -> {
            List<SearchAttr> searchAttrList = skuAttrValueMapper.findSkuAttrAndValueBySkuId(skuId);
            goods.setAttrs(searchAttrList);
        });

        // 让上述的4个异步线程执行完毕以后，在进行返回
        CompletableFuture.allOf(cf1 , cf2 , cf3 , cf4).join() ;

        // 返回
        return goods ;

    }

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;


    // 创建一个线程池，可以执行定时任务的线程池
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5) ;

    /**
     * 创建一个线程的方式：
     * 1、继承Thread类Runnable
     * 2、实现Runnable接口
     * 3、实现Callable接口
     * 4、使用线程池
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {

        // 删除redis中的数据
//        redisTemplate.delete(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId) ;
//        log.info("从redis中删除了数据, skuId: {}" , skuId);

        // 业务操作，更新数据库
        SkuInfo skuInfo = new SkuInfo() ;
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        updateById(skuInfo) ;
        log.info("对数据库中的数据进行了修改, skuId: {}" , skuId);

        // 延迟删除
//        scheduledThreadPoolExecutor.schedule(() -> {
//
//            // 删除redis中的数据
//            redisTemplate.delete(GmallConstant.REDIS_SKU_DETAIL_PREFIX + skuId) ;
//            log.info("执行了延迟删除redis中的数据的操作 skuId: {}" , skuId);
//
//        } , 300 , TimeUnit.MILLISECONDS) ;        // 延迟300毫秒进行执行

        // 远程调用service-search微服务中删除数据的方法
        goodsFeignClient.deleteGoodsById(skuId) ;

    }

    @Autowired
    private RedissonClient redissonClient ;

    /**
     * 初始化分布式bloomFilter
     */
    @PostConstruct
    public void init() {

        // 查询所有的skuId
        List<Long> skuIds = skuInfoMapper.findAllSkuIds();

        // 通过RessionClient创建bloomFilter
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(GmallConstant.REDSI_BLOOMFILTER_SKU_DETAIL);

        // 判断这个bloomFilter是否存在，如果不存在则进行初始化
        if(!bloomFilter.isExists()) {

            bloomFilter.tryInit(1000000 , 0.000001) ;

            // skuId存储到bloomFilter
            skuIds.forEach(skuId -> {
                bloomFilter.add(skuId) ;
            });

        }

        log.info("布隆过滤器初始化完毕，判断skuId为49的商品在bloomFilter是否存在：" + bloomFilter.contains(49L));
        log.info("布隆过滤器初始化完毕，判断skuId为100的商品在bloomFilter是否存在：" + bloomFilter.contains(100L));

    }

}




