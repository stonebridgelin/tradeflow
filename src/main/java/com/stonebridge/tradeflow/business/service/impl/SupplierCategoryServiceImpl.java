package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.supplier.SupplierCategory;
import com.stonebridge.tradeflow.business.mapper.SupplierCategoryMapper;
import com.stonebridge.tradeflow.business.service.SupplierCategoryService;
import org.springframework.stereotype.Service;

@Service
public class SupplierCategoryServiceImpl extends ServiceImpl<SupplierCategoryMapper, SupplierCategory> implements SupplierCategoryService {
}
