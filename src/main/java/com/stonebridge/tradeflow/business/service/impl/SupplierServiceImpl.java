package com.stonebridge.tradeflow.business.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.supplier.Supplier;
import com.stonebridge.tradeflow.business.mapper.SupplierMapper;
import com.stonebridge.tradeflow.business.service.SupplierService;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    private final DataDictionaryService dataDictionaryService;

    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierServiceImpl(DataDictionaryService dataDictionaryService, SupplierMapper supplierMapper) {
        this.dataDictionaryService = dataDictionaryService;
        this.supplierMapper = supplierMapper;
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
}
