package com.atguigu.gmall.rabbit.anno;

import com.atguigu.gmall.rabbit.biz.impl.RabbitBizServiceImpl;
import com.atguigu.gmall.rabbit.config.RabbitMQConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)           // 当前这个自定义注解的使用位置为启动类
@Retention(value = RetentionPolicy.RUNTIME)  // 生效时期
@Import(value = { RabbitMQConfiguration.class , RabbitBizServiceImpl.class})
public @interface EnableRabbit {

}
