package com.stonebridge.tradeflow.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;

public interface AttrGroupService extends IService<AttrGroup> {
    Page<AttrGroup> queryPage(String currentPage, String pageSize, String categoryId, String keyword);
}
