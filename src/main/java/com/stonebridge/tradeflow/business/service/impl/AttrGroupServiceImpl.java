package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrGroupDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrGroupVO;
import com.stonebridge.tradeflow.business.mapper.AttrGroupMapper;
import com.stonebridge.tradeflow.business.mapper.AttrMapper;
import com.stonebridge.tradeflow.business.service.AttrAttrgroupRelationService;
import com.stonebridge.tradeflow.business.service.AttrGroupService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    JdbcTemplate jdbcTemplate;
    MyRedisCache myRedisCache;
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    public AttrGroupServiceImpl(@Qualifier("businessJdbcTemplate") JdbcTemplate jdbcTemplate, MyRedisCache myRedisCache, AttrAttrgroupRelationService attrAttrgroupRelationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.myRedisCache = myRedisCache;
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
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
            if (StringUtils.isNotBlank(currentPage)) {
                pageNum = Integer.parseInt(currentPage);
            }
            if (StringUtils.isNotBlank(limit)) {
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
        if (StringUtils.isNotBlank(categoryId)) {
            wrapper.eq(AttrGroup::getCategoryId, categoryId);
        }

        // 添加关键词模糊查询条件
        if (StringUtils.isNotBlank(keyWord)) {
            wrapper.like(AttrGroup::getAttrGroupName, keyWord);
        }

        // 添加排序条件，按sort字段升序排列
        wrapper.orderByAsc(AttrGroup::getSort);

        // 执行分页查询,查询结果为：groupPage
        Page<AttrGroup> groupPage = this.page(page, wrapper);
        Page<AttrGroupVO> attrGroupVOPage = new Page<>();
        BeanUtils.copyProperties(groupPage, attrGroupVOPage);

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
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveAttrGroup(AttrGroup attrGroup) {
        try {
            // 验证 sortValue
            Integer sortValue = attrGroup.getSort();
            if (sortValue == null) {
                throw new IllegalArgumentException("sort cannot be null");
            }

            // 查询 sort >= attrGroup.sort 的记录（加锁）
            String sql = "SELECT attr_group_id, sort FROM pms_attr_group WHERE sort >= ? AND category_id = ? FOR UPDATE";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, sortValue, attrGroup.getCategoryId());

            if (!list.isEmpty()) {
                // 更新 sort >= attrGroup.sort 的记录
                sql = "UPDATE pms_attr_group SET sort = sort + 1 WHERE sort >= ? AND category_id = ?";
                jdbcTemplate.update(sql, sortValue, attrGroup.getCategoryId());
            }
            // 插入新记录
            attrGroup.setAttrGroupId(null);
            super.save(attrGroup);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save AttrGroup: " + e.getMessage(), e);
        }
    }

    /**
     * 更新AttrGroup，处理 sort 变化
     *
     * @param attrGroup 包含更新属性的 AttrGroup对象,但是sort为前端传来为最新的
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateAttrGroup(AttrGroup attrGroup) {
        String categoryId = StringUtils.trim(attrGroup.getCategoryId());
        // 获取当前数据库中的attrGroup以获取旧的 sort 值
        AttrGroup existingAttrGroup = this.getById(attrGroup.getAttrGroupId());
        if (existingAttrGroup == null) {
            throw new IllegalArgumentException("属性分组不存在");
        }
        //获取旧的sort值
        Integer oldSort = existingAttrGroup.getSort();

        //获取旧的新的值
        Integer newSort = attrGroup.getSort();
        if (newSort == null || newSort <= 0) {
            throw new IllegalArgumentException("sort 必须为正整数");
        }

        try {
            // 锁定受影响的记录
            String lockSql;
            if (oldSort < newSort) {
                lockSql = "SELECT attr_group_id FROM pms_attr_group WHERE sort > ? AND sort <= ? AND category_id = ? FOR UPDATE";
                jdbcTemplate.queryForList(lockSql, oldSort, newSort, categoryId);
            } else if (oldSort > newSort) {
                lockSql = "SELECT attr_group_id FROM pms_attr_group WHERE sort >= ? AND sort < ? AND category_id = ? FOR UPDATE";
                jdbcTemplate.queryForList(lockSql, newSort, oldSort, categoryId);
            }

            jdbcTemplate.update("DELETE FROM pms_attr_group WHERE attr_group_id = ?", attrGroup.getAttrGroupId());
            String updateSql;

            //根据旧排序位置的比较，进行更新
            if (oldSort < newSort) {
                // 旧 sort < 新 sort，更新 [oldSort, newSort]
                updateSql = "UPDATE pms_attr_group SET sort = sort - 1 WHERE sort > ? AND sort <= ? AND category_id = ?";
                jdbcTemplate.update(updateSql, oldSort, newSort, categoryId);
            } else if (oldSort > newSort) {
                // 新 sort < 旧 sort，更新 [newSort, oldSort]
                updateSql = "UPDATE pms_attr_group SET sort = sort + 1 WHERE sort >= ? AND sort < ? AND category_id = ?";
                jdbcTemplate.update(updateSql, newSort, oldSort, categoryId);
            }
            super.save(attrGroup);
        } catch (Exception e) {
            throw new RuntimeException("更新AttrGroup失败，可能存在主键冲突: " + e.getMessage(), e);
        }
    }

    /**
     * @param attrGroupId :pms_attr_group的主键id
     * @return ：AttrGroup信息+ CategoryName
     */
    @Override
    public AttrGroupVO selectAttrGroupById(String attrGroupId) {
        AttrGroup attrGroup = this.getById(attrGroupId);
        AttrGroupVO attrGroupVO = new AttrGroupVO();
        BeanUtils.copyProperties(attrGroup, attrGroupVO);
        attrGroupVO.setCategoryName(myRedisCache.getCategoryNameById(attrGroup.getCategoryId()));
        return attrGroupVO;
    }

    @Override
    public Integer getSortRangeByCatId(String catId) {
        String sql = "SELECT COUNT(1) from pms_attr_group WHERE category_id=?";
        int rows = jdbcTemplate.queryForObject(sql, Integer.class, catId);
        if (rows < 1) {
            rows = 1;
        }
        return rows;
    }


    @Override
    public List<Map<String, Object>> getAttrGroupListByCatId(String catId) {
        String sql = "SELECT attr_group_id as attrGroupId,attr_group_name as attrGroupName from pms_attr_group WHERE category_id=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, catId);
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override
    public void deleteAttrGroup(String id) {
        this.removeById(id);
        attrAttrgroupRelationService.deleteAttrGroupRelation(id);
    }
}
