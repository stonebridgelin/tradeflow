package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.supplier.dto.SupplierDetail;
import com.stonebridge.tradeflow.business.entity.supplier.Supplier;
import com.stonebridge.tradeflow.common.result.Result;

public interface SupplierService extends IService<Supplier> {
    Result<Object> getSupplierList(int currentPage, int pageSize, String keyword);

    void deleteSupplierDetailById(String id);

    void saveSupplierDetail(SupplierDetail supplierDetail);

    void updateSupplierDetail(SupplierDetail supplierDetail);

    SupplierDetail getSupplierDetailById(String id);
}
