package com.stonebridge.tradeflow.business.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.Category;
import com.stonebridge.tradeflow.business.entity.dto.SupplierDetail;
import com.stonebridge.tradeflow.business.entity.supplier.*;
import com.stonebridge.tradeflow.business.mapper.*;
import com.stonebridge.tradeflow.business.service.SupplierService;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    private final DataDictionaryService dataDictionaryService;

    private final SupplierMapper supplierMapper;

    private final SupplierCategoryMapper supplierCategoryMapper;

    private final BankAccountMapper bankAccountMapper;

    private final PlatformInfoMapper platformInfoMapper;

    private final SalespersonMapper salespersonMapper;

    private final CategoryMapper categoryMapper;

    @Autowired
    public SupplierServiceImpl(DataDictionaryService dataDictionaryService, SupplierMapper supplierMapper, SupplierCategoryMapper supplierCategoryMapper
            , BankAccountMapper bankAccountMapper, PlatformInfoMapper platformInfoMapper, SalespersonMapper salespersonMapper, CategoryMapper categoryMapper) {
        this.dataDictionaryService = dataDictionaryService;
        this.supplierMapper = supplierMapper;
        this.supplierCategoryMapper = supplierCategoryMapper;
        this.bankAccountMapper = bankAccountMapper;
        this.platformInfoMapper = platformInfoMapper;
        this.salespersonMapper = salespersonMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Result<Object> getSupplierList(int currentPage, int pageSize, String keyword) {

        try {
            // 构建分页对象
            Page<Supplier> page = new Page<>(currentPage, pageSize);
            // 构建查询条件
            QueryWrapper<Supplier> wrapper = new QueryWrapper<>();

            // 添加排序条件：按 type 升序，同一 type 内按 update_time 降序
            wrapper.orderByDesc("update_time");
            // 执行分页查询
            Page<Supplier> pageResult = this.page(page, wrapper);
            for (Supplier supplier : pageResult.getRecords()) {
                DataDictionary dataDictionary = dataDictionaryService.getByTypeAndCode(Constant.DATA_DICTIONARY_SUPPLIER_TYPE, supplier.getSupplierType());
                if (dataDictionary != null) {
                    supplier.setSupplierType(dataDictionary.getName());
                }
            }
            return Result.ok(pageResult);
        } catch (Exception e) {
            return Result.fail("分页查询失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteSupplierById(String id) {
        // 1. Validate the ID
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Supplier ID cannot be empty");
        }
        Integer supplierId;
        try {
            supplierId = Integer.valueOf(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Supplier ID format: " + id);
        }

        // 3. Perform logical delete (MyBatis-Plus automatically handles this due to @TableLogic)
        int rowsAffected = supplierMapper.deleteById(supplierId);
        if (rowsAffected == 0) {
            throw new RuntimeException("Failed to delete supplier with ID: " + id);
        }
    }

    //更新供应商的信息到主表pms_supplier表，以及其他从表中
    @Transactional
    public void updateSupplierDetail(SupplierDetail supplierDetail) {

    }

    //保存供应商的信息到主表pms_supplier表，以及其他从表中
    @Transactional
    @Override
    public void saveSupplierDetail(SupplierDetail supplierDetail) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDetail, supplier);
        supplier.setSupplierCode(IdUtil.fastSimpleUUID());
        supplier.setStatus(Constant.SUPPLIER_STATUS_ENABLE);
        //1.保存供应商的主要信息到主表pms_supplier表
        supplierMapper.insert(supplier);
        Integer supplierId = supplier.getId();


        List<String> categories = supplierDetail.getCategoris();
        if (categories != null && !categories.isEmpty()) {
            //2.保存供应商的产品分类信息到pms_supplier_category表
            for (String category : categories) {
                if (StrUtil.isNotEmpty(category)) {
                    SupplierCategory supplierCategory = new SupplierCategory();
                    supplierCategory.setSupplierId(String.valueOf(supplierId));
                    supplierCategory.setCategoryId(category);
                    supplierCategory.setUpdateTime(new Date());
                    supplierCategory.setCreateTime(new Date());
                    supplierCategoryMapper.insert(supplierCategory);
                }
            }
        }

        List<BankAccount> bankAccountList = supplierDetail.getBankAccount();
        if (bankAccountList != null && !bankAccountList.isEmpty()) {
            //3.保存供应商的银行账号到pms_bank_account表
            for (BankAccount bankAccount : bankAccountList) {
                bankAccount.setSupplierId(String.valueOf(supplierId));
                bankAccount.setUpdateTime(new Date());
                bankAccount.setCreateTime(new Date());
                bankAccountMapper.insert(bankAccount);
            }
        }

        List<PlatformInfo> platformInfoList = supplierDetail.getPlatformInfo();
        if (platformInfoList != null && !platformInfoList.isEmpty()) {
            //4.保存供应商的网上平台信息到pms_platform_info表
            for (PlatformInfo platformInfo : platformInfoList) {
                platformInfo.setSupplierId(String.valueOf(supplierId));
                platformInfo.setUpdateTime(new Date());
                platformInfo.setCreateTime(new Date());
                platformInfoMapper.insert(platformInfo);
            }
        }

        List<Salesperson> salespersonList = supplierDetail.getSalesperson();
        if (salespersonList != null && !salespersonList.isEmpty()) {
            //4.保存供应商的业务员信息到pms_salesperson表
            for (Salesperson salesperson : salespersonList) {
                salesperson.setSupplierId(String.valueOf(supplierId));
                salesperson.setUpdateTime(new Date());
                salesperson.setCreateTime(new Date());
                salespersonMapper.insert(salesperson);
            }
        }


    }

    @Override
    public SupplierDetail getSupplierDetailById(String id) {
        if (StrUtil.isNotEmpty(id)) {
            //1.从主表pms_supplier表获取供应商的主要信息
            Supplier supplier = supplierMapper.selectById(id);
            SupplierDetail supplierDetail = new SupplierDetail();
            BeanUtils.copyProperties(supplier, supplierDetail);


            List<SupplierCategory> supplierCategoryList = supplierCategoryMapper.selectList(new QueryWrapper<SupplierCategory>().eq("supplier_id", id));
            if (supplierCategoryList != null && !supplierCategoryList.isEmpty()) {
                //2.1.从pms_supplier_category表获取供应商与产品分类信息管理信息
                String categoryId;
                Category category;
                Map<String, String> categoryNode = new HashMap<>();
                //2.2.从关系信息表获取具体的分类信息，显示给用户
                for (SupplierCategory supplierCategory : supplierCategoryList) {
                    categoryId = supplierCategory.getCategoryId();
                    category = categoryMapper.selectById(categoryId);
                    supplierDetail.getCategoris().add(categoryId);

                    categoryNode.put("id", category.getId());
                    categoryNode.put("name", category.getName());
                    supplierDetail.getCategoryNodes().add(categoryNode);
                }
            }

            List<BankAccount> bankAccountList = bankAccountMapper.selectList(new QueryWrapper<BankAccount>().eq("supplier_id", id));
            if (bankAccountList != null && !bankAccountList.isEmpty()) {
                //3.从pms_bank_account表获取供应商的银行账号信息
                supplierDetail.setBankAccount(bankAccountList);
            }

            List<PlatformInfo> platformInfoList = platformInfoMapper.selectList(new QueryWrapper<PlatformInfo>().eq("supplier_id", id));
            if (platformInfoList != null && !platformInfoList.isEmpty()) {
                //4.从pms_platform_info表获取供应商的网上平台信息
                supplierDetail.setPlatformInfo(platformInfoList);
            }

            List<Salesperson> salespersonList = salespersonMapper.selectList(new QueryWrapper<Salesperson>().eq("supplier_id", id));
            if (salespersonList != null && !salespersonList.isEmpty()) {
                //5.从pms_salesperson表获取供应商的业务员信息到
                supplierDetail.setSalesperson(salespersonList);
            }
            return supplierDetail;

        }
        return null;
    }
}
