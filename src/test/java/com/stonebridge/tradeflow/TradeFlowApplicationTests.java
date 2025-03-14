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
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootTest
class TradeFlowApplicationTests {

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


    // 测试 MyBatis-Plus 访问 system_db
    @Test
    public void testMyBatisPlusSystemDb() {
        SystemUser user = systemUserService.getById(1); // MyBatis-Plus 查询
        System.out.println("MyBatis-Plus (system_db): " + user.getUserName());
    }

    // 测试 JdbcTemplate 访问 system_db
    @Test
    public void testJdbcTemplateSystemDb() {
        String username = systemUserService.getUsernameByIdJdbc(1L); // JdbcTemplate 查询
        System.out.println("JdbcTemplate (system_db): " + username);
    }

    // 测试 MyBatis-Plus 访问 business_db
    @Test
    public void testMyBatisPlusBusinessDb() {
        Product product = productService.getById(43); // MyBatis-Plus 查询
        System.out.println("MyBatis-Plus (business_db): " + product.getProductName());
    }

    // 测试 JdbcTemplate 访问 business_db
    @Test
    public void testJdbcTemplateBusinessDb() {
        String productName = productService.getProductNameByIdJdbc(43); // JdbcTemplate 查询
        System.out.println("JdbcTemplate (business_db): " + productName);
    }

}
