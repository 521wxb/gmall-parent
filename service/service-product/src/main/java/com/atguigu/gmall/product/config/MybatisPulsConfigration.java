package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MybatisPulsConfigration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        /**
         * MybatisPlusInterceptor是插件主体，管理所有的插件
         */
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        /**
         * 创建一个分页插件
         */
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true);  // 溢出总页数后是否进行处理
        interceptor.addInnerInterceptor(paginationInnerInterceptor);  // 把分页插件设置给插件主体
        return interceptor;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource) ;
    }

}
