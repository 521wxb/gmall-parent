package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.gateway.entity.AuthUrlProperties;
import com.atguigu.gmall.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.xml.ws.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserAuthFilter implements GlobalFilter  , Ordered {

    @Autowired
    private AuthUrlProperties authUrlProperties ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    // 创建一个匹配器
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher() ;

    /**
     * 拦截请求
     * @param exchange: 封装了请求对象和响应对象
     * @param chain： 就是过滤器链，通过该对象可以实现放行操作
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // log.info("UserAuthFilter...filter...方法执行了...");

        // 从exchange对象中获取请求对象
        String path = exchange.getRequest().getURI().getPath();

        /**
         * 这个方法是可以拦截所有的请求，但是我们并不是说所有请求资源都需要让用户进行登录。有一些资源用户不登录也可以访问。
         * 如果访问的就是不用登录就可以访问的资源，那么此时应该放行。
         * 1. 定义不要验证登录的资源路径
         * 2. 获取真实请求的资源
         * 3. 判断真实请求的资源路径是否满足第一步所定义资源路径的规则，如果满足直接放行
         */
        List<String> noauthurl = authUrlProperties.getNoauthurl();
        for(String urlPattern : noauthurl) {
            if(antPathMatcher.match(urlPattern , path)) {
                log.info("请求的资源路径为：{}" , path);
                return chain.filter(exchange);      // 匹配成功以后直接进行放行
            }
        }

        /**
         * 针对哪些需要验证登录的请求路径，进行登录的验证。 如果登录就直接放行，如果没有登录直接踢回到登录页面
         */
        List<String> authurl = authUrlProperties.getAuthurl();
        for(String authUrlPattern : authurl) {
            if(antPathMatcher.match(authUrlPattern, path)) {        // 验证登录操作

                // 获取token
                String token = getUserToken(exchange) ;
                if(StringUtils.isEmpty(token)) {
                    return locationUrl(exchange , path) ;
                }else {

                    // 根据token从Redis中查询用户数据
                    String userInfoJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_USER_LOGIN_PREFIX + token);
                    if(StringUtils.isEmpty(userInfoJson)) {
                        return locationUrl(exchange , path) ;
                    }else {  // 可以查询到数据，直接放行, 并且用户id的透传
                        return userIdThrought(userInfoJson , exchange , chain) ;
                    }

                }

            }
        }

        // 普通请求，可以登录也不登录
        String userToken = getUserToken(exchange);
        if(StringUtils.isEmpty(userToken)) {
            return tempUserIdThrought(exchange , chain) ;
        }else {

            // 验证token的合法性
            String userInfoJson = redisTemplate.opsForValue().get(GmallConstant.REDIS_USER_LOGIN_PREFIX + userToken);
            if(!StringUtils.isEmpty(userInfoJson)) { // 进行放行操作，并且进行id的透传
                return  userIdThrought(userInfoJson , exchange , chain) ;
            }else {  // 伪造的token
                return locationUrl(exchange , path) ;
            }

        }

    }

    /**
     * 临时用户id的透传
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> tempUserIdThrought(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取临时用户的id
        String tempUserId = getTempUserId(exchange) ;

        // 进行透传
        ServerHttpRequest httpRequest = exchange.getRequest().mutate().header("userTempId", tempUserId).build();
        ServerWebExchange serverWebExchange = exchange.mutate().request(httpRequest).response(exchange.getResponse()).build();

        // 放行操作
        return chain.filter(serverWebExchange) ;

    }

    // 进行用户的id的透传
    public Mono<Void> userIdThrought(String userInfoJson , ServerWebExchange exchange , GatewayFilterChain chain ) {

        // 获取用户的id
        UserInfo userInfo = JSON.parseObject(userInfoJson, UserInfo.class);
        Long userId = userInfo.getId();

        // 获取当前未登录用户的临时用户的id
        String tempUserId = getTempUserId(exchange);

        // 需要重新构建request对象
        ServerHttpRequest httpRequest = exchange.getRequest().mutate()
                .header("userId", String.valueOf(userId))
                .header("userTempId" , tempUserId)
                .build();

        // 重新构建一个ServerWebExchange
        ServerHttpResponse response = exchange.getResponse();
        ServerWebExchange webExchange = exchange.mutate().request(httpRequest).response(response).build();

        // 对登录用户在Redis中的存活时间进行续期
        String token = getUserToken(exchange);
        redisTemplate.expire(GmallConstant.REDIS_USER_LOGIN_PREFIX + token , 30 , TimeUnit.DAYS) ;

        // 返回
        return chain.filter(webExchange) ;

    }

    // 去登录页面
    private Mono<Void> locationUrl(ServerWebExchange exchange , String path) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);    // 设置响应状态码
        response.getHeaders().add("location" , authUrlProperties.getLoginPageUrl() + "?originUrl=" + path);


        // 清空cookie中的token
        ResponseCookie responseCookie = ResponseCookie.from("token", "1").
                domain(".gmall.com").maxAge(0).path("/").build();
        response.addCookie(responseCookie);

        return response.setComplete();
    }

    // 获取临时用户的id
    private String getTempUserId(ServerWebExchange exchange) {

        /**
         * 前端传递临时用户的id存在两种方式：通过Cookie进行传递，第二种方式通过header
         */
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, HttpCookie> requestCookies = request.getCookies();
        HttpCookie httpCookie = requestCookies.getFirst("userTempId");
        String userTempId = "" ;
        if(httpCookie != null) {
            userTempId = httpCookie.getValue();
        }else {
            userTempId = request.getHeaders().getFirst("userTempId");
        }
        return userTempId ;

    }

    // 获取token数据
    private String getUserToken(ServerWebExchange exchange) {

        /**
         * 前端传递token的时候有两种方式：
         * 1、通过Cookie进行传递
         * 2、通过header进行传递
         */
        ServerHttpRequest request = exchange.getRequest();      // 获取request对象
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie httpCookie = cookies.getFirst("token");
        String token = "" ;
        if(httpCookie != null) {
            token = httpCookie.getValue();  // 获取到了令牌
        }else {
            token = request.getHeaders().getFirst("token");
        }
        return token ;

    }

    @Override
    public int getOrder() {     // 定义过滤器的顺序 ， 数字越小，过滤器的优先级越高
        return -1;
    }

}
