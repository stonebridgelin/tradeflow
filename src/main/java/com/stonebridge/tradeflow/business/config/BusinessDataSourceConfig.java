package com.stonebridge.tradeflow.business.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BusinessDataSourceConfig {

    @Bean(name = "businessDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.business")
    public DataSource businessDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "businessSqlSessionFactory")
    public MybatisSqlSessionFactoryBean businessSqlSessionFactory(
            @Qualifier("businessDataSource") DataSource dataSource,
            @Qualifier("businessPaginationInterceptor") MybatisPlusInterceptor interceptor) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPlugins(interceptor); // 绑定分页拦截器

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:mapper/business/**/*.xml");
        if (resources != null && resources.length > 0) {
            factoryBean.setMapperLocations(resources);
            System.out.println("Loaded " + resources.length + " XML files for businessSqlSessionFactory");
        }
        return factoryBean;
    }

    @Bean(name = "businessJdbcTemplate")
    public JdbcTemplate businessJdbcTemplate(@Qualifier("businessDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}