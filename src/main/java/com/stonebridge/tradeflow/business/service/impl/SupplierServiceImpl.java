package com.stonebridge.tradeflow.business.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.Category;
import com.stonebridge.tradeflow.business.entity.dto.CategoryNode;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public SupplierServiceImpl(DataDictionaryService dataDictionaryService, SupplierMapper supplierMapper,
                               SupplierCategoryMapper supplierCategoryMapper, BankAccountMapper bankAccountMapper,
                               PlatformInfoMapper platformInfoMapper, SalespersonMapper salespersonMapper,
                               CategoryMapper categoryMapper) {
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

    @Transactional
    @Override
    public void updateSupplierDetail(SupplierDetail supplierDetail) {
        if (supplierDetail == null || StrUtil.isEmpty(supplierDetail.getId())) {
            throw new IllegalArgumentException("SupplierDetail 或 ID 不能为空");
        }

        String supplierId = supplierDetail.getId();
        Supplier existingSupplier = supplierMapper.selectById(supplierId);
        if (existingSupplier == null) {
            throw new IllegalArgumentException("供应商 ID " + supplierId + " 不存在");
        }

        // 更新主表
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDetail, supplier);
        supplier.setId(Integer.parseInt(supplierId));
        supplier.setUpdateTime(new Date());
        supplierMapper.updateById(supplier);

        // 删除旧的从表记录
        supplierCategoryMapper.delete(new QueryWrapper<SupplierCategory>().eq("supplier_id", supplierId));
        bankAccountMapper.delete(new QueryWrapper<BankAccount>().eq("supplier_id", supplierId));
        platformInfoMapper.delete(new QueryWrapper<PlatformInfo>().eq("supplier_id", supplierId));
        salespersonMapper.delete(new QueryWrapper<Salesperson>().eq("supplier_id", supplierId));

        // 插入新的从表记录
        insertCategories(supplierDetail.getCategories(), supplierId);
        insertBankAccounts(supplierDetail.getBankAccount(), supplierId);
        insertPlatformInfo(supplierDetail.getPlatformInfo(), supplierId);
        insertSalespersons(supplierDetail.getSalesperson(), supplierId);
    }

    @Transactional
    @Override
    public void deleteSupplierDetailById(String id) {
        if (StrUtil.isEmpty(id)) {
            throw new IllegalArgumentException("供应商 ID 不能为空");
        }

        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new IllegalArgumentException("供应商 ID " + id + " 不存在");
        }

        supplierCategoryMapper.delete(new QueryWrapper<SupplierCategory>().eq("supplier_id", id));
        bankAccountMapper.delete(new QueryWrapper<BankAccount>().eq("supplier_id", id));
        platformInfoMapper.delete(new QueryWrapper<PlatformInfo>().eq("supplier_id", id));
        salespersonMapper.delete(new QueryWrapper<Salesperson>().eq("supplier_id", id));
        supplierMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void saveSupplierDetail(SupplierDetail supplierDetail) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDetail, supplier);
        supplier.setSupplierCode(IdUtil.fastSimpleUUID());
        supplier.setStatus(Constant.SUPPLIER_STATUS_ENABLE);
        supplier.setCreateTime(new Date());
        supplier.setUpdateTime(new Date());
        supplierMapper.insert(supplier);
        String supplierId = String.valueOf(supplier.getId());

        insertCategories(supplierDetail.getCategories(), supplierId);
        insertBankAccounts(supplierDetail.getBankAccount(), supplierId);
        insertPlatformInfo(supplierDetail.getPlatformInfo(), supplierId);
        insertSalespersons(supplierDetail.getSalesperson(), supplierId);
    }

    @Override
    public SupplierDetail getSupplierDetailById(String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }

        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            return null;
        }

        SupplierDetail supplierDetail = new SupplierDetail();
        BeanUtils.copyProperties(supplier, supplierDetail);

        // 初始化所有列表字段为非 null 空数组
        supplierDetail.setCategories(new ArrayList<>());
        supplierDetail.setCategoryNodes(new ArrayList<>());
        supplierDetail.setBankAccount(new ArrayList<>());
        supplierDetail.setPlatformInfo(new ArrayList<>());
        supplierDetail.setSalesperson(new ArrayList<>());

        // 产品分类
        List<SupplierCategory> supplierCategoryList = supplierCategoryMapper.selectList(
                new QueryWrapper<SupplierCategory>().eq("supplier_id", id));
        for (SupplierCategory supplierCategory : supplierCategoryList) {
            String categoryId = supplierCategory.getCategoryId();
            if (StrUtil.isNotEmpty(categoryId)) {
                Category category = categoryMapper.selectById(categoryId);
                if (category != null) {
                    supplierDetail.getCategories().add(categoryId);
                    CategoryNode categoryNode = new CategoryNode();
                    categoryNode.setId(categoryId);
                    categoryNode.setName(category.getName());
                    supplierDetail.getCategoryNodes().add(categoryNode);
                }
            }
        }

        // 银行账户
        List<BankAccount> bankAccountList = bankAccountMapper.selectList(
                new QueryWrapper<BankAccount>().eq("supplier_id", id));
        supplierDetail.setBankAccount(bankAccountList != null ? bankAccountList : new ArrayList<>());

        // 平台信息
        List<PlatformInfo> platformInfoList = platformInfoMapper.selectList(
                new QueryWrapper<PlatformInfo>().eq("supplier_id", id));
        supplierDetail.setPlatformInfo(platformInfoList != null ? platformInfoList : new ArrayList<>());

        // 业务员信息
        List<Salesperson> salespersonList = salespersonMapper.selectList(
                new QueryWrapper<Salesperson>().eq("supplier_id", id));
        supplierDetail.setSalesperson(salespersonList != null ? salespersonList : new ArrayList<>());

        return supplierDetail;
    }

    // 插入产品分类
    private void insertCategories(List<String> categories, String supplierId) {
        if (categories != null && !categories.isEmpty()) {
            for (String categoryId : categories) {
                if (StrUtil.isNotEmpty(categoryId)) {
                    SupplierCategory supplierCategory = new SupplierCategory();
                    supplierCategory.setSupplierId(supplierId);
                    supplierCategory.setCategoryId(categoryId);
                    supplierCategory.setCreateTime(new Date());
                    supplierCategory.setUpdateTime(new Date());
                    supplierCategoryMapper.insert(supplierCategory);
                }
            }
        }
    }

    // 插入银行账户
    private void insertBankAccounts(List<BankAccount> bankAccountList, String supplierId) {
        if (bankAccountList != null && !bankAccountList.isEmpty()) {
            for (BankAccount bankAccount : bankAccountList) {
                bankAccount.setSupplierId(supplierId);
                bankAccount.setCreateTime(new Date());
                bankAccount.setUpdateTime(new Date());
                bankAccountMapper.insert(bankAccount);
            }
        }
    }

    // 插入平台信息
    private void insertPlatformInfo(List<PlatformInfo> platformInfoList, String supplierId) {
        if (platformInfoList != null && !platformInfoList.isEmpty()) {
            for (PlatformInfo platformInfo : platformInfoList) {
                platformInfo.setSupplierId(supplierId);
                platformInfo.setCreateTime(new Date());
                platformInfo.setUpdateTime(new Date());
                platformInfoMapper.insert(platformInfo);
            }
        }
    }

    // 插入业务员信息
    private void insertSalespersons(List<Salesperson> salespersonList, String supplierId) {
        if (salespersonList != null && !salespersonList.isEmpty()) {
            for (Salesperson salesperson : salespersonList) {
                salesperson.setSupplierId(supplierId);
                salesperson.setCreateTime(new Date());
                salesperson.setUpdateTime(new Date());
                salespersonMapper.insert(salesperson);
            }
        }
    }
}