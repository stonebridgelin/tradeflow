package com.stonebridge.tradeflow.business.controller;

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
        supplierService.deleteSupplierById(id);
        return Result.ok("删除成功");

    }
}
