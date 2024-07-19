package com.atguigu.gmall.cart.biz.impl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.biz.CartBizService;
import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.product.entity.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 向Redis中存储购物车数据的时候，应该存储哪些数据？
 * 取决于前端要展示哪些数据
 */
@Slf4j
@Service
public class CartBizServiceImpl implements CartBizService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient ;

    /**
     * Redis中购物车数据key： cart:info:userId   cart:info:userTempId
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public AddCartSuccessVo addCart(Long skuId, Integer skuNum) {

        // 构建购物车的key
        String cartKey = buildCartKey() ;

        // 根据skuId的查询skuInfo的数据
        Result<SkuInfo> skuInfoResult = skuDetailFeignClient.findSkuInfoBySkuId(skuId);
        SkuInfo skuInfo = skuInfoResult.getData();

        // 从Redis中获取用户的购物车的购物项数据
        Object obj =  redisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));
        if( obj == null) {     // 购物车数据为空

            // 获取到所有购物车中的购物项  // 购物项的数量不要超过3
            List<Object> objectList = redisTemplate.opsForHash().values(cartKey);
            if(objectList != null && objectList.size() >= 3) {  // 现在购物车中的购物项的数量最多2
                throw new GmallException(ResultCodeEnum.SKUITEM_CATGRAY_COUNT) ;
            }

            // 购物项的数量限制
            if(skuNum >= 10) {
                skuNum = 10 ;
            }

            // 构建CartItem对象
            CartItem cartItem = new CartItem() ;
            cartItem.setId(skuInfo.getId());
            cartItem.setSkuId(skuInfo.getId());
            cartItem.setCartPrice(new BigDecimal(skuInfo.getPrice()));          // 向购物车中添加商品的是所对应的商品的价格
            cartItem.setSkuPrice(new BigDecimal(skuInfo.getPrice()));           // sku商品最新价格
            cartItem.setSkuNum(skuNum);
            cartItem.setImgUrl(skuInfo.getSkuDefaultImg());
            cartItem.setSkuName(skuInfo.getSkuName());
            cartItem.setIsChecked(1);
            cartItem.setCreateTime(new Date());
            cartItem.setUpdateTime(new Date());

            // 把CartItem对象存储到购物车中
            redisTemplate.opsForHash().put(cartKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));

        }else {     // 购物车数据已经存在了

            String cartItemJson = (String) obj ;
            CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
            cartItem.setSkuNum(cartItem.getSkuNum() + skuNum);

            // 判断合并之后的数据
            if(cartItem.getSkuNum() > 10) {
                cartItem.setSkuNum(10);
            }

            // 查询最新的sku的价格
            cartItem.setSkuPrice(new BigDecimal(skuInfo.getPrice()));

            // 把CartItem对象存储到购物车中
            redisTemplate.opsForHash().put(cartKey , String.valueOf(skuId)  , JSON.toJSONString(cartItem));

        }

        // 判断当前用户是否登录
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        if(userAuthInfo.getUserId() == null) { // 操作的就是临时购物车

            // 获取临时购物车的存活时间
            Long expire = redisTemplate.getExpire(GmallConstant.REDIS_USER_CART_PREFIX + userAuthInfo.getUserTempId());
            if(expire < 0) {
                redisTemplate.expire(GmallConstant.REDIS_USER_CART_PREFIX + userAuthInfo.getUserTempId() , 30 , TimeUnit.DAYS) ;
            }

        }

        // 返回AddCartSuccessVo对象
        AddCartSuccessVo addCartSuccessVo = new AddCartSuccessVo() ;
        addCartSuccessVo.setSkuInfo(skuInfo);
        addCartSuccessVo.setSkuNum(skuNum);

        // 返回
        return addCartSuccessVo;
    }

    @Override
    public List<CartItem> findCartList() {

        // 获取当前登录用户
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();
        if(userId != null) {  // 登录了

            // 将临时购物车中的数据合并到用户车中
            // 购物车临时购物车的key
            String redisUserTempIdCartKey = GmallConstant.REDIS_USER_CART_PREFIX + userAuthInfo.getUserTempId() ;

            // 用户购物车key
            String redisUserIdCartKey = GmallConstant.REDIS_USER_CART_PREFIX + userAuthInfo.getUserId() ;

            // 获取临时购物车
            List<CartItem> cartTempList = redisTemplate.opsForHash().values(redisUserTempIdCartKey).stream()
                    .map(obj -> {
                        String cartItemJson = obj.toString();
                        CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
                        return cartItem;
                    }).collect(Collectors.toList());

            // 购物项的数量进行判断
            // 获取到了用户的临时购物车购物项，然后在获取到用户的购物车的购物项的数据，然后做累加，如果数量超过3，那么此时就直接抛出异常
            if(cartTempList != null && cartTempList.size() > 0) {

                Set<Long> tmepCartItemListSize = cartTempList.stream().map(cartItem -> cartItem.getSkuId()).collect(Collectors.toSet());
                Set<Long> keys = redisTemplate.opsForHash().keys(redisUserIdCartKey).stream().map(skuId -> Long.parseLong(skuId.toString()))
                        .collect(Collectors.toSet());

                // 进行集合的合并
                tmepCartItemListSize.addAll(keys);

                // 对集合的长度进行判断
                int size = tmepCartItemListSize.size();
                if(size > 3) {
                    throw new GmallException(ResultCodeEnum.SKUITEM_CATGRAY_COUNT) ;
                }

            }

            log.info("用户已经进行了登录操作...获取到了用户的临时购物车数据, 准备进行购物车数据的合并...");

            // 对临时购物车的数据进行遍历
            for(CartItem cartItem : cartTempList) {

                // 判断当前遍历的这个购物项数据在用户的购物车中是否存在
                Long skuId = cartItem.getSkuId();
                Boolean hasKey = redisTemplate.opsForHash().hasKey(redisUserIdCartKey, String.valueOf(skuId));
                if(hasKey) {     // 包含购物项数据

                    // 从用户购物车中获取该购物项数据
                    String cartItemJson = redisTemplate.opsForHash().get(redisUserIdCartKey, String.valueOf(skuId)).toString();
                    CartItem item = JSON.parseObject(cartItemJson, CartItem.class);

                    // 把当前遍历的购物项数据的skuNum获取出来加入到item对象的skuNum字段上
                    item.setSkuNum(item.getSkuNum() + cartItem.getSkuNum());

                    // 对购物项的数量进行限定
                    if(item.getSkuNum() > 10) {
                        item.setSkuNum(10);
                    }

                    // 把该购物项再次存储到购物车数据
                    redisTemplate.opsForHash().put(redisUserIdCartKey , String.valueOf(skuId) , JSON.toJSONString(item));

                }else  {        // 不包含购物项
                    redisTemplate.opsForHash().put(redisUserIdCartKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));
                }

            }

            log.info("用户已经进行了登录操作...获取到了用户的临时购物车数据, 购物车的数据合并完毕了...");

            // 删除用户临时购物车
            redisTemplate.delete(redisUserTempIdCartKey) ;

            log.info("用户已经进行了登录操作...购物车数据合并完毕，并且清空临时购物车中的数据...");

            // 返回购物车列表数据
            List<CartItem> itemList = redisTemplate.opsForHash().values(redisUserIdCartKey).stream().map(obj -> {
                String cartItemJson = obj.toString();
                CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
                updateCartItemPriceReturn(cartItem.getSkuId() , cartItem , redisUserIdCartKey) ;      // 查询最新的价格
                return cartItem;
            }).sorted((c1, c2) -> (int) (c1.getCreateTime().getTime() - c2.getCreateTime().getTime()))
                    .collect(Collectors.toList());

            // 返回购物车列表数据
            return itemList ;

        }else {              // 没有登录

            // 购物车临时购物车的key
            String redisUserTempIdCartKey = GmallConstant.REDIS_USER_CART_PREFIX + userAuthInfo.getUserTempId() ;

            // 根据cartKey获取购物车数据
            List<Object> list = redisTemplate.opsForHash().values(redisUserTempIdCartKey);
            List<CartItem> itemList = list.stream().map(obj -> {
                String cartItemJson = (String) obj;
                CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
                updateCartItemPriceReturn(cartItem.getSkuId() , cartItem , redisUserTempIdCartKey) ;      // 查询最新的价格
                return cartItem;

            }).sorted((c1, c2) -> (int) (c1.getCreateTime().getTime() - c2.getCreateTime().getTime())).collect(Collectors.toList());

            log.info("用户没有登录，获取到了用户的临时购物车的数据，然后进行返回...");

            return itemList ;
        }

    }

    // 在返回购物车列表数据的时候，查询商品的最新价格进行返回
    private void updateCartItemPriceReturn(Long skuId , CartItem cartItem , String redisCartKey) {

        // 查询商品的最新价格
        Result<SkuInfo> skuInfoResult = skuDetailFeignClient.findSkuInfoBySkuId(cartItem.getSkuId());
        SkuInfo skuInfo = skuInfoResult.getData();
        cartItem.setSkuPrice(new BigDecimal(skuInfo.getPrice()));
        log.info("从数据库中查询到商品的最新的价格数据，然后返回最新的价格数据，skuId: {}" , skuId);

        // 更新Redis中的购物项的价格为最新价格
        redisTemplate.opsForHash().put(redisCartKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));
        log.info("从数据库中查询到商品的最新的价格数据，然后更新了Redis中购物项的价格数据，skuId: {}" , skuId);

    }

    @Override
    public void addToCart(Long skuId, Integer skuNum) {

        // 获取用户在redis中存储的购物车的key
        String cartKey = buildCartKey();

        // 获取对应的购物车的购物项
        String cartItemJson = (String) redisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));

        // 转换成CartItem对象
        CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
        cartItem.setSkuNum( cartItem.getSkuNum() + skuNum);

        // 对购物项的数量进行控制
        if(cartItem.getSkuNum() > 10) {
            cartItem.setSkuNum(10);
        }

        // 把更新完成以后的购物车数据再次存储到Redis中
        redisTemplate.opsForHash().put(cartKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));

    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {

        // 获取用户在redis中存储的购物车的key
        String cartKey = buildCartKey();

        // 获取对应的购物车的购物项
        String cartItemJson = (String) redisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));

        // 转换成CartItem对象
        CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
        cartItem.setIsChecked(isChecked);

        // 把更新完成以后的购物车数据再次存储到Redis中
        redisTemplate.opsForHash().put(cartKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));

    }

    @Override
    public void deleteCart(Long skuId) {

        // 获取用户在redis中存储的购物车的key
        String cartKey = buildCartKey();

        // 对Redis的hash进行操作，删除指定的购物项
        redisTemplate.opsForHash().delete(cartKey , String.valueOf(skuId)) ;
    }

    @Override
    public void deleteCheckedCart() {

        // 获取用户在redis中存储的购物车的key
        String cartKey = buildCartKey();

        // 获取购物车中的所有的购物项数据
        redisTemplate.opsForHash().values(cartKey).stream().forEach( cartItemJson -> {
            String strJson = (String) cartItemJson ;
            CartItem cartItem = JSON.parseObject(strJson, CartItem.class);
            if(cartItem.getIsChecked() == 1) {
                redisTemplate.opsForHash().delete(cartKey , String.valueOf(cartItem.getSkuId())) ;
            }
        } );

    }

    @Override
    public List<CartItem> getCheckedCartItem() {

        // 获取要操作的购物车的key
        String cartKey = buildCartKey();

        // 根据购物车的key获取所有的购物项数据
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey).stream()
                .map(obj -> {
                    String cartItemJson = obj.toString();
                    CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
                    return cartItem;
                }).filter(cartItem -> cartItem.getIsChecked() == 1).collect(Collectors.toList());

        return cartItemList;
    }

    // 构建购物车的key
    private String buildCartKey() {

        // 获取用户的id以及临时用户的id
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();
        if(userId != null) {
            return GmallConstant.REDIS_USER_CART_PREFIX + userId ;
        }else {
            return GmallConstant.REDIS_USER_CART_PREFIX + userAuthInfo.getUserTempId() ;
        }

    }

}
