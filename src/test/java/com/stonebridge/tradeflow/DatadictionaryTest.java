package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.business.service.*;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@SpringBootTest
public class DatadictionaryTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Test
    public void TestDataByType() {
        String type = "bank_account";
        List<DataDictionary> list = dataDictionaryService.getByType(type);
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    public void TestDataByTypeCode() {
        String type = "bank_account";
        String code = "020";
        DataDictionary dictionary = dataDictionaryService.getByTypeAndCode(type, code);
        System.out.println(dictionary);
    }


    @Autowired
    private SupplierService supplierService;

    @Test
    public void TestSupplier() {
        supplierService.list().forEach(System.out::println);
    }

    @Autowired
    private SupplierCategoryService supplierCategoryService;

    @Test
    public void TestSupplierCategory() {
        supplierCategoryService.list().forEach(System.out::println);
    }

    @Autowired
    private SalespersonService salespersonService;

    @Test
    public void TestSalesperson() {
        salespersonService.list().forEach(System.out::println);
    }

    @Autowired
    private PlatformInfoService platformInfoService;

    @Test
    public void TestPlatformInfo() {
        platformInfoService.list().forEach(System.out::println);
    }

    @Autowired
    private BankAccontService bankAccontService;

    @Test
    public void TestBankAccount() {
        bankAccontService.list().forEach(System.out::println);
    }
}
