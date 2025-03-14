package com.stonebridge.tradeflow.system.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class SystemDataSourceConfig {

    @Primary
    @Bean(name = "systemDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.system")
    public DataSource systemDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "systemSqlSessionFactory")
    @Primary
    public MybatisSqlSessionFactoryBean systemSqlSessionFactory(@Qualifier("systemDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:mapper/system/*.xml");
        if (resources != null && resources.length > 0) {
            factoryBean.setMapperLocations(resources);
            System.out.println("Loaded " + resources.length + " XML files for systemSqlSessionFactory");
        }
        return factoryBean;
    }

    @Bean(name = "systemJdbcTemplate")
    @Primary
    public JdbcTemplate systemJdbcTemplate(@Qualifier("systemDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}