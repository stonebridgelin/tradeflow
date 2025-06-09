package com.stonebridge.tradeflow.business.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.category.Category;
import com.stonebridge.tradeflow.business.entity.supplier.dto.CategoryNode;
import com.stonebridge.tradeflow.business.entity.supplier.dto.SupplierDetail;
import com.stonebridge.tradeflow.business.entity.supplier.*;
import com.stonebridge.tradeflow.business.entity.supplier.vo.SupplierVO;
import com.stonebridge.tradeflow.business.mapper.*;
import com.stonebridge.tradeflow.business.service.SupplierService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {


    private final SupplierMapper supplierMapper;

    private final SupplierCategoryMapper supplierCategoryMapper;

    private final BankAccountMapper bankAccountMapper;

    private final PlatformInfoMapper platformInfoMapper;

    private final SalespersonMapper salespersonMapper;

    private final MyRedisCache myRedisCache;

    @Autowired
    public SupplierServiceImpl(SupplierMapper supplierMapper,
                               SupplierCategoryMapper supplierCategoryMapper, BankAccountMapper bankAccountMapper,
                               PlatformInfoMapper platformInfoMapper, SalespersonMapper salespersonMapper,
                               MyRedisCache myRedisCache) {
        this.supplierMapper = supplierMapper;
        this.supplierCategoryMapper = supplierCategoryMapper;
        this.bankAccountMapper = bankAccountMapper;
        this.platformInfoMapper = platformInfoMapper;
        this.salespersonMapper = salespersonMapper;
        this.myRedisCache = myRedisCache;
    }


    @Override
    public Result<Object> getSupplierList(int currentPage, int pageSize, String keyword) {
        try {
            // 构建分页对象
            Page<Supplier> page = new Page<>(currentPage, pageSize);
            // 构建查询条件
            QueryWrapper<Supplier> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("update_time");

            // 执行分页查询
            Page<Supplier> pageResult = this.page(page, wrapper);
            if (pageResult.getRecords().isEmpty()) {
                // 如果无数据，直接返回空分页结果
                Page<SupplierVO> emptyPage = new Page<>();
                BeanUtils.copyProperties(pageResult, emptyPage, "records");
                emptyPage.setRecords(new ArrayList<>());
                return Result.ok(emptyPage);
            }

            // 1.获取所有的Supplier对象
            List<Supplier> records = pageResult.getRecords();
            // 用于保存所有Supplier对象的supplierType数据，避免重复
            Set<String> supplierTypeSet = new HashSet<>();
            // 用于保存所有是SupplierCategory对象
            List<SupplierCategory> supplierCategoryList = new ArrayList<>();
            // 用于保存每个supplier对应的SupplierCategory对象集合
            Map<String, List<SupplierCategory>> supplierCategoryMap = new HashMap<>(); // 修复1：键类型从 Integer 改为 String

// 遍历所有的Supplier对象，将需要的supplierTypeSet、supplierCategoryList、supplierCategoryMap赋值
            // 收集所有 Supplier ID
            List<Integer> supplierIds = new ArrayList<>();
            for (Supplier supplier : records) {
                supplierTypeSet.add(supplier.getSupplierType());
                supplierIds.add(supplier.getId());
            }

            // 一次性查询所有 SupplierCategory
            QueryWrapper<SupplierCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("supplier_id", supplierIds);
            List<SupplierCategory> supplierCategories = supplierCategoryMapper.selectList(queryWrapper);
            supplierCategoryList.addAll(supplierCategories);

            // 按 supplier_id 分组
            for (SupplierCategory supplierCategory : supplierCategories) {
                String supplierId = supplierCategory.getSupplierId();
                supplierCategoryMap.computeIfAbsent(supplierId, k -> new ArrayList<>()).add(supplierCategory);
            }

            // 将所有涉及的DataDictionary保存在Map中，通过supplier.supplierType作为key
            Map<String, DataDictionary> dataDictionaryMap = new HashMap<>();
            for (String supplierType : supplierTypeSet) {
                dataDictionaryMap.put(supplierType, myRedisCache.getDataDictionaryByTypeAndCode(Constant.DATA_DICTIONARY_SUPPLIER_TYPE, supplierType));
            }
            // 将所有涉及的Category保存在Map中，通过categoryId作为key
            Map<String, Category> categoryMap = new HashMap<>();
            for (SupplierCategory supplierCategory : supplierCategoryList) {
                categoryMap.put(supplierCategory.getCategoryId(), myRedisCache.getCategoryById(supplierCategory.getCategoryId()));
            }

            // 转换 Supplier 数据并添加额外数据
            List<SupplierVO> voRecords = pageResult.getRecords().stream().map(supplier -> {
                // 将 Supplier 转换为 DTO
                SupplierVO vo = new SupplierVO();
                BeanUtils.copyProperties(supplier, vo);

                // 设置 supplierType 名称（已有逻辑）
                DataDictionary dataDictionary = dataDictionaryMap.get(supplier.getSupplierType());
                if (dataDictionary != null) {
                    vo.setSupplierType(dataDictionary.getName());
                }
                // 添加不属于 Supplier 的额外数据，即分类的信息
                List<SupplierCategory> list = supplierCategoryMap.getOrDefault(String.valueOf(supplier.getId()), new ArrayList<>()); // 修复2：使用 getOrDefault 避免 NPE
                for (SupplierCategory supplierCategory : list) {
                    String categoryId = supplierCategory.getCategoryId();
                    Category category = categoryMap.get(categoryId);
                    if (category != null) {
                        if (vo.getCategories() == null) {
                            vo.setCategories(new ArrayList<>());
                        }
                        vo.getCategories().add(category.getName());
                    }
                }
                return vo;
            }).collect(Collectors.toList());

            // 创建新的 Page 对象，承载 DTO 数据
            Page<SupplierVO> dtoPageResult = new Page<>();
            BeanUtils.copyProperties(pageResult, dtoPageResult, "records"); // 复制分页信息
            dtoPageResult.setRecords(voRecords);

            return Result.ok(dtoPageResult);
        } catch (Exception e) {
            log.error("Failed to query supplier list. CurrentPage: {}, PageSize: {}, Keyword: {}. Error: {}",
                    currentPage, pageSize, keyword, e.getMessage(), e); // 修复3：记录详细错误日志
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
                Category category = myRedisCache.getCategoryById(categoryId);
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