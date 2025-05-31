package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.supplier.Supplier;
import com.stonebridge.tradeflow.business.mapper.SupplierMapper;
import com.stonebridge.tradeflow.business.service.SupplierService;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {
}
