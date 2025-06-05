package com.stonebridge.tradeflow.business.controller;

import com.stonebridge.tradeflow.business.entity.dto.SupplierDetail;
import com.stonebridge.tradeflow.business.service.SupplierService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {
    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("list/{currentPage}/{pageSize}")
    public Result<Object> getSupplierList(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, String keyword) {
        Result<Object> supplierRecords = supplierService.getSupplierList(currentPage, pageSize, keyword);
        return supplierRecords;
    }

    @DeleteMapping("delete/{id}")
    public Result<Object> deleteSupplier(@PathVariable(value = "id") String id) {
        supplierService.deleteSupplierDetailById(id);
        return Result.ok("删除成功");
    }

    @PostMapping("add")
    public Result<Object> addSupplier(@RequestBody SupplierDetail supplierDetail) {
        supplierService.saveSupplierDetail(supplierDetail);
        return Result.ok("添加成功");
    }


    @PutMapping("update")
    public Result<Object> updateSupplierDetail(@RequestBody SupplierDetail supplierDetail) {
        supplierService.updateSupplierDetail(supplierDetail);
        return Result.ok("更新成功");
    }

    @GetMapping("supplierDetail/{id}")
    public Result<SupplierDetail> getSupplierDetail(@PathVariable(value = "id") String id) {
        SupplierDetail supplierDetail = supplierService.getSupplierDetailById(id);
        return Result.ok(supplierDetail);
    }
}
