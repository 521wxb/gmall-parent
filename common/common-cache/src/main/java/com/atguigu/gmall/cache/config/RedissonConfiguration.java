package com.atguigu.gmall.cache.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {

    @Autowired
    private RedisProperties redisProperties ;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config() ;          // 创建一个Redisson的配置对象
        config.useSingleServer().setAddress("redis://" +  redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword()) ;
        RedissonClient redissonClient = Redisson.create(config);        // 创建RedissonClient对象
        return redissonClient ;
    }

}
