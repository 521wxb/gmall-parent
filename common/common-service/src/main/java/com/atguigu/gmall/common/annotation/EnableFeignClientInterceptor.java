package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.interceptor.FeignClientInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)   // 当前这个自定义注解的使用位置为类上
@Retention(value = RetentionPolicy.RUNTIME)  // 生效时期
@Import(value = FeignClientInterceptor.class)
public @interface EnableFeignClientInterceptor {

}
