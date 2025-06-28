package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrGroupDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrGroupVO;
import com.stonebridge.tradeflow.business.mapper.AttrGroupMapper;
import com.stonebridge.tradeflow.business.service.AttrGroupService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    JdbcTemplate jdbcTemplate;
    MyRedisCache myRedisCache;


    @Autowired
    public AttrGroupServiceImpl(@Qualifier("businessJdbcTemplate") JdbcTemplate jdbcTemplate, MyRedisCache myRedisCache) {
        this.jdbcTemplate = jdbcTemplate;
        this.myRedisCache = myRedisCache;
    }

    /**
     * 根据查询条件查询符合条件的分页的AttrGroup数据
     *
     * @param attrGroupDTO 条件封装
     * @return ：分页的AttrGroup数据
     */
    @Override
    public Page<AttrGroupVO> queryPage(AttrGroupDTO attrGroupDTO) {
        //当前页面
        String currentPage = attrGroupDTO.getPage();
        //每页数据数量
        String limit = attrGroupDTO.getLimit();
        //和attr_group_name自动进行模糊匹配的关键词
        String keyWord = attrGroupDTO.getKeyword();
        //分类的id，对应attrgroup.category_id
        String categoryId = attrGroupDTO.getCategoryId();
        // 参数校验和转换
        int pageNum = 1;
        int size = 10;

        try {
            if (StringUtils.hasText(currentPage)) {
                pageNum = Integer.parseInt(currentPage);
            }
            if (StringUtils.hasText(limit)) {
                size = Integer.parseInt(limit);
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
        if (StringUtils.hasText(keyWord)) {
            wrapper.like(AttrGroup::getAttrGroupName, keyWord);
        }

        // 添加排序条件，按sort字段升序排列
        wrapper.orderByAsc(AttrGroup::getSort);

        // 执行分页查询,查询结果为：groupPage
        Page<AttrGroup> groupPage = this.page(page, wrapper);
        Page<AttrGroupVO> attrGroupVOPage = new Page<>();
        attrGroupVOPage.setCurrent(groupPage.getCurrent());
        attrGroupVOPage.setSize(groupPage.getSize());
        attrGroupVOPage.setTotal(groupPage.getTotal());

        //将groupPage的records数据拿出来，将CategoryId转化为CategoryName保存到AttrGroupVO对象，返回到前端
        List<AttrGroup> records = groupPage.getRecords();
        List<AttrGroupVO> attrGroupVOList = new ArrayList<>();
        for (AttrGroup attrGroup : records) {
            AttrGroupVO attrGroupVO = new AttrGroupVO();
            BeanUtils.copyProperties(attrGroup, attrGroupVO);
            attrGroupVO.setCategoryName(myRedisCache.getCategoryNameById(attrGroup.getCategoryId()));
            attrGroupVOList.add(attrGroupVO);
        }
        attrGroupVOPage.setRecords(attrGroupVOList);
        return attrGroupVOPage;
    }

    /**
     * 保存AttrGroup对象数据
     *
     * @param attrGroup AttrGroup对象
     * @return 保存结果
     */
    @Override
    public boolean save(AttrGroup attrGroup) {
        String sql = "SELECT COUNT(1) from pms_attr_group WHERE category_id=?";
        Integer rows = jdbcTemplate.queryForObject(sql, Integer.class, attrGroup.getCategoryId());
        attrGroup.setSort(rows + 1);
        attrGroup.setAttrGroupId(null);
        return super.save(attrGroup);
    }

    /**
     * 根据
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public AttrGroupVO selectAttrGroupById(String attrGroupId) {
        AttrGroup attrGroup = this.getById(attrGroupId);
        AttrGroupVO attrGroupVO = new AttrGroupVO();
        BeanUtils.copyProperties(attrGroup, attrGroupVO);
        attrGroupVO.setCategoryName(myRedisCache.getCategoryNameById(attrGroup.getCategoryId()));
        return attrGroupVO;
    }
}
