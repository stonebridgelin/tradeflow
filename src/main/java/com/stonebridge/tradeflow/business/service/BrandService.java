package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.brand.Brand;
import com.stonebridge.tradeflow.common.result.Result;

import java.util.List;

public interface BrandService extends IService<Brand> {
    Result<Object> queryBrandList(int currentPage, int pageSize, String keyword);

    void updateStatus(String brandId, String newStatus);

    void delete(String id);

    void createBrand(Brand brand);

    void updateBrand(Brand brand);

    List<Brand> queryBrandByCategoryId(String categoryId, String keyWord);
}
