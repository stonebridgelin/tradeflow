package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.brand.Brand;
import com.stonebridge.tradeflow.business.mapper.BrandMapper;
import com.stonebridge.tradeflow.business.service.BrandService;
import com.stonebridge.tradeflow.business.service.CategoryBrandRelationService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    private final JdbcTemplate jdbcTemplate;
    private final BrandMapper brandMapper;
    private final MyRedisCache myRedisCache;
    private final CategoryBrandRelationService categoryBrandRelationService;

    public BrandServiceImpl(@Qualifier("businessJdbcTemplate") JdbcTemplate jdbcTemplate, BrandMapper brandMapper, CategoryBrandRelationService categoryBrandRelationService,MyRedisCache myRedisCache) {
        this.jdbcTemplate = jdbcTemplate;
        this.brandMapper = brandMapper;
        this.myRedisCache = myRedisCache;
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    @Override
    public Result<Object> queryBrandList(int currentPage, int pageSize, String keyword) {
        try {
            Page<Brand> page = new Page<>(currentPage, pageSize);
            QueryWrapper<Brand> wrapper = new QueryWrapper<>();
            wrapper.like("name", keyword);
            wrapper.orderByDesc("sort");

            Page<Brand> pageResult = this.page(page, wrapper);
            return Result.ok(pageResult);
        } catch (Exception e) {
            log.error("Failed to query Brand list. CurrentPage: {}, PageSize: {}, Keyword: {}. Error: {}", currentPage, pageSize, keyword, e.getMessage(), e); // 修复3：记录详细错误日志
            return Result.fail("分页查询失败：" + e.getMessage());
        }
    }

    @Override
    public void updateStatus(String brandId, String newStatus) {
        String updateSql = "UPDATE pms_brand set show_status=? where id=?";
        jdbcTemplate.update(updateSql, Integer.parseInt(newStatus), brandId);
        this.refreshRedisCache();
    }

    @Override
    public void delete(String id) {
        brandMapper.deleteById(id);
        this.refreshRedisCache();
    }

    @Override
    public void createBrand(Brand brand) {
        String sql = "SELECT MAX(sort) FROM pms_brand;";
        Integer maxSort = jdbcTemplate.queryForObject(sql, Integer.class);
        if (maxSort == null) {
            maxSort = 0;
        }
        brand.setSort(maxSort + 1);
        brand.setId(null);

        brandMapper.insert(brand);
        this.refreshRedisCache();
    }

    @Override
    public void updateBrand(Brand brand) {
        this.brandMapper.updateById(brand);
        this.refreshRedisCache();

    }

    /**
     * 根据分类的id（categoryId），先根据categoryId从pms_category_brand_relation查询对应的brand的id，再查询brand的详细信息
     * @param categoryId :被选中的categoryId
     * @param keyWord ：与brand.name匹配的关键词
     * @return ：查询结果
     */
    @Override
    public List<Brand> queryBrandByCategoryId(String categoryId, String keyWord) {
        List<String> brandIds = categoryBrandRelationService.queryBrandIdsByCategoryId(categoryId);
        QueryWrapper<Brand> wrapper = new QueryWrapper<>();
        keyWord = StringUtils.trim(keyWord);
        if (keyWord != null && !keyWord.isEmpty()) {
            wrapper.like("name", keyWord);
        }
        if (!brandIds.isEmpty()) {
            wrapper.in("id", brandIds);
            wrapper.orderByDesc("sort");
            return this.list(wrapper);
        }else {
            return null;
        }
    }

    /**
     * 刷新的brand的redis缓存
     */
    public void refreshRedisCache() {
        myRedisCache.refreshCache(MyRedisCache.CacheConstants.TYPE_BRAND);
    }
}
