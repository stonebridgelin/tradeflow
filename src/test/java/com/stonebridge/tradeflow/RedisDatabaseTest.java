package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.business.entity.Product;
import com.stonebridge.tradeflow.business.service.ProductService;
import com.stonebridge.tradeflow.system.entity.SystemUser;
import com.stonebridge.tradeflow.system.service.SystemUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisDatabaseTest {

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("systemJdbcTemplate")
    private JdbcTemplate systemJdbcTemplate;

    @Autowired
    @Qualifier("businessJdbcTemplate")
    private JdbcTemplate businessJdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    public void setUp() {
        // 清空 Redis 和数据库
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        // 初始化 system_db 数据
        systemJdbcTemplate.update("DELETE FROM sys_user");
        systemJdbcTemplate.update("INSERT INTO sys_user (id, user_name, nick_name, password, status) " +
                "VALUES (1, 'testUser', 'Test', '123456', '0')");

        // 初始化 business_db 数据
        businessJdbcTemplate.update("DELETE FROM products");
        businessJdbcTemplate.update("INSERT INTO products (product_id, product_name, brand, created_by) " +
                "VALUES (1, 'testProduct', 'testBrand', 1)");
    }

    // 测试 MyBatis-Plus 和 Redis 访问 system_db
    @Test
    public void testMyBatisPlusSystemDbWithRedis() {
        SystemUser user = systemUserService.getByIdWithCache(1L);
        assertNotNull(user);
        assertEquals("testUser", user.getUserName());
        System.out.println("MyBatis-Plus with Redis (system_db): " + user.getUserName());

        // 验证 Redis 缓存
        SystemUser cachedUser = (SystemUser) redisTemplate.opsForValue().get("system:user:1");
        assertNotNull(cachedUser);
        assertEquals("testUser", cachedUser.getUserName());
    }

    // 测试 JdbcTemplate 访问 system_db
    @Test
    public void testJdbcTemplateSystemDb() {
        String username = systemUserService.getUsernameByIdJdbc(1L);
        assertEquals("testUser", username);
        System.out.println("JdbcTemplate (system_db): " + username);
    }

    // 测试 MyBatis-Plus 和 Redis 访问 business_db
    @Test
    public void testMyBatisPlusBusinessDbWithRedis() {
        Product product = productService.getByIdWithCache(1);
        assertNotNull(product);
        assertEquals("testProduct", product.getProductName());
        System.out.println("MyBatis-Plus with Redis (business_db): " + product.getProductName());

        // 验证 Redis 缓存
        Product cachedProduct = (Product) redisTemplate.opsForValue().get("business:product:1");
        assertNotNull(cachedProduct);
        assertEquals("testProduct", cachedProduct.getProductName());
    }

    // 测试 JdbcTemplate 访问 business_db
    @Test
    public void testJdbcTemplateBusinessDb() {
        String productName = productService.getProductNameByIdJdbc(1);
        assertEquals("testProduct", productName);
        System.out.println("JdbcTemplate (business_db): " + productName);
    }

    // 测试 Redis 缓存失效
    @Test
    public void testRedisCacheEviction() {
        // 第一次查询，存入缓存
        systemUserService.getByIdWithCache(1L);
        // 清除缓存
        redisTemplate.delete("system:user:1");
        // 第二次查询，应重新从数据库加载
        SystemUser user = systemUserService.getByIdWithCache(1L);
        assertNotNull(user);
        assertEquals("testUser", user.getUserName());
        System.out.println("Redis cache evicted and reloaded (system_db): " + user.getUserName());
    }
}