package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.business.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void test() {
        categoryService.list().forEach(System.out::println);
    }
}
