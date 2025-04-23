package com.stonebridge.tradeflow.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置类
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = "com.stonebridge.tradeflow.system.mapper", sqlSessionFactoryRef = "systemSqlSessionFactory")
@MapperScan(basePackages = "com.stonebridge.tradeflow.business.mapper", sqlSessionFactoryRef = "businessSqlSessionFactory")
public class MybatisPlusConfig {

    // 分页拦截器 - System 数据源
    @Bean
    public MybatisPlusInterceptor systemPaginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // system 数据源为 MySQL，可替换为其他数据库类型
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    // 分页拦截器 - Business 数据源
    @Bean
    public MybatisPlusInterceptor businessPaginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // business 数据源为 MySQL，可替换为其他数据库类型
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}