package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.Product;
import com.stonebridge.tradeflow.business.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {
    private final JdbcTemplate businessJdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductService(@Qualifier("businessJdbcTemplate") JdbcTemplate businessJdbcTemplate,
                          RedisTemplate<String, Object> redisTemplate) {
        this.businessJdbcTemplate = businessJdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    public Product getByIdWithCache(Integer id) {
        String key = "business:product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(key);

        if (product == null) {
            product = baseMapper.selectById(id); // MyBatis-Plus 查询
            if (product != null) {
                redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS); // 缓存 1 小时
            }
        }
        return product;
    }

    public String getProductNameByIdJdbc(Integer id) {
        return businessJdbcTemplate.queryForObject(
                "SELECT product_name FROM products WHERE product_id = ?", String.class, id);
    }
}