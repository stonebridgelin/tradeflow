package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.mapper.AttrGroupMapper;
import com.stonebridge.tradeflow.business.service.AttrGroupService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    /**
     * @param currentPage :当前页面
     * @param pageSize    ：每页数据数量
     * @param categoryId  ：分类的id，对应attrgroup.category_id
     * @param keyword     :和attr_group_name自动进行模糊匹配的关键词
     * @return :分页的AttrGroup数据
     */
    @Override
    public Page<AttrGroup> queryPage(String currentPage, String pageSize, String categoryId, String keyword) {
        // 参数校验和转换
        int pageNum = 1;
        int size = 10;
        
        try {
            if (StringUtils.hasText(currentPage)) {
                pageNum = Integer.parseInt(currentPage);
            }
            if (StringUtils.hasText(pageSize)) {
                size = Integer.parseInt(pageSize);
            }
        } catch (NumberFormatException e) {
            // 如果转换失败，使用默认值
        }
        
        // 确保页码和页大小在合理范围内
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100;
        }
        
        // 构建分页对象
        Page<AttrGroup> page = new Page<>(pageNum, size);
        
        // 构建查询条件
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        
        // 添加分类ID条件
        if (StringUtils.hasText(categoryId)) {
            wrapper.eq(AttrGroup::getCategoryId, categoryId);
        }
        
        // 添加关键词模糊查询条件
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AttrGroup::getAttrGroupName, keyword);
        }
        
        // 添加排序条件，按sort字段升序排列
        wrapper.orderByAsc(AttrGroup::getSort);
        
        // 执行分页查询
        return this.page(page, wrapper);
    }
}
