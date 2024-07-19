package com.atguigu.gmall.common.constant;

public interface GmallConstant {

    /**
     * redis中缓存的三级分类的key
     */
    public static final String REDIS_KEY_CATEGORY = "categoryList" ;

    /**
     * 缓存数据库中不存在的数据的值
     */
    public static final String REDIS_NULL_VALUE = "X" ;

    /**
     * redis中缓存的sku的详情数据的key的前缀
     */
    public static final String REDIS_SKU_DETAIL_PREFIX = "sku:detail:" ;

    /**
     * 查询sku详情数据，防止缓存击穿对应分布式锁的前缀
     */
    public static final String REDIS_SKU_DETAIL_LOCK_PREFIX = "item-lock:" ;

    /**
     * 分布式锁的过期时间
     */
    public static final Integer REDIS_SKU_DETAIL_LOCK_TIMEOUT = 30 ;

    /**
     * 分布式bloomFilter
     */
    public static final String REDSI_BLOOMFILTER_SKU_DETAIL = "sku-detail-bloomfilter" ;

    /**
     * 新的分布式bloomFilter的名称
     */
    public static final String REDSI_BLOOMFILTER_SKU_DETAIL_NEW = "sku-detail-bloomfilter-new" ;

    /**
     * 定义redis中商品热度分的前缀
     */
    public static final String REDSI_SKU_HOTSCORE_PREFIX = "sku:hotscore:" ;

    /**
     * 用户登录成功以后的redis中保存的key的前缀
     */
    public static final String REDIS_USER_LOGIN_PREFIX = "user:login:" ;

    /**
     * 用户购物车的key的前缀
     */
    public static final String REDIS_USER_CART_PREFIX = "cart:info:" ;

    /**
     * 防止订单数据重复提交所对应的redis的key
     */
    public static final String REDIS_ORDER_CONFIRM_PREFIX = "order:info:confirm:" ;

    /**
     * 存储秒杀商品的key
     */
    public static final String REDIS_SECKILL_GOODS_PREFIX = "seckill:goods:" ;

    /**
     * 定义缓存预热的分布式锁的名称
     */
    public static final String REDIS_CACEH_PRE_HOST = "seckill-redis-lock" ;

    /**
     * 用户秒杀码key
     */
    public static final String REDIS_SECKILL_CODE_PREFIX = "seckill:code:" ;

    /**
     * redis商品库存
     */
    public static final String REDIS_SECKILL_GOODS_STOCK_COUNT_PREFIX = "seckill:goods:stockcount:" ;

    /**
     * 秒杀商品的订单的key
     */
    public static final String REDIS_SECKILL_ORDER_PREFIX = "seckill:goods:order:" ;

    /**
     * 秒杀商品的订单的值为NO
     */
    public static final String REDIS_SECKILL_ORDER_NO_STOCK = "NO" ;

}
