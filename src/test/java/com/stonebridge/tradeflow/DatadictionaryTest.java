package com.stonebridge.tradeflow;

import com.stonebridge.tradeflow.business.service.*;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DatadictionaryTest {

    @Autowired
    private MyRedisCache redisCache;

    @Test
    public void TestDataByType() {
        List<DataDictionary> list = redisCache.getDataDictionaryByType(Constant.DATA_DICTIONARY_BANK_TYPE);
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    public void TestDataByTypeCode() {
//        String code = "020";
//        DataDictionary dict = redisCache.getDataDictionaryByTypeAndCode("platform", "050");
        DataDictionary dictionary = redisCache.getDataDictionaryByTypeAndCode("platform", "050");
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
