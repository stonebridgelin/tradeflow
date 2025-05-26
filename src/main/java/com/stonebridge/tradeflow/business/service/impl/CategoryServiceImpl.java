package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.Category;
import com.stonebridge.tradeflow.business.mapper.CategoryMapper;
import com.stonebridge.tradeflow.business.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
