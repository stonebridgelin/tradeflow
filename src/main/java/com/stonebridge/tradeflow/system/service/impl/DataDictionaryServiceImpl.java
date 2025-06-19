package com.stonebridge.tradeflow.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.mapper.DataDictionaryMapper;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements DataDictionaryService {

    private DataDictionaryMapper dataDictionaryMapper;

    private MyRedisCache myRedisCache;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DataDictionaryServiceImpl(DataDictionaryMapper dataDictionaryMapper, MyRedisCache myRedisCache, @Qualifier("systemJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.dataDictionaryMapper = dataDictionaryMapper;
        this.myRedisCache = myRedisCache;
        this.jdbcTemplate = jdbcTemplate;
    }


    public Result<Object> getDataDictionaryPage(int currentPage, int pageSize, String keyword, String type) {

        try {
            // 构建分页对象
            Page<DataDictionary> page = new Page<>(currentPage, pageSize);

            // 构建查询条件
            QueryWrapper<DataDictionary> wrapper = new QueryWrapper<>();

            // 添加 type 精确匹配条件
            if (StringUtils.isNotBlank(type)) {
                wrapper.eq("type", type);
            }

            // 添加 keyword 模糊匹配条件（匹配 type, name, comment）
            if (StringUtils.isNotBlank(keyword)) {
                wrapper.and(w -> w.like("type", keyword)
                        .or().like("name", keyword)
                        .or().like("comment", keyword));
            }

            // 添加排序条件：按 type 升序，同一 type 内按 update_time 降序
            wrapper.orderByAsc("type")
                    .orderByDesc("update_time");

            // 执行分页查询
            Page<DataDictionary> pageResult = this.page(page, wrapper);
            return Result.ok(pageResult);
        } catch (Exception e) {
            return Result.fail("分页查询失败：" + e.getMessage());
        }
    }

    public Result<ObjectNode> saveOrUpdateDt(DataDictionary dt) {
        Integer id = dt.getId();
        //如果id不为空，则是更新操作，从数据库获取源数据，将新的属性拷贝到源数据，然后在数据库中完成更新操作。
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("code", "200");
        if (id != null) {
            DataDictionary dataDictionary = dataDictionaryMapper.selectById(id);
            BeanUtils.copyProperties(dt, dataDictionary);
            dataDictionary.setUpdateTime(new Date());
            dataDictionaryMapper.updateById(dataDictionary);
        } else {
            //如果id为空，则是新增操作，先判断数据库中是否已存在相同的type和code，如果存在则返回错误信息，否则执行新增操作。
            Integer rows = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM data_dictionary WHERE type=? and code=?", Integer.class, dt.getType(), dt.getCode());
            if (rows > 0) {
                json.put("code", "500");
                json.put("message", "新增的数据已存在相同的type和name");
                return Result.ok(json);
            }
            dt.setCreateTime(new Date());
            dt.setUpdateTime(new Date());
            dataDictionaryMapper.insert(dt);
        }
        myRedisCache.refreshCache(MyRedisCache.CacheConstants.TYPE_DATA_DICTIONARY);
        return Result.ok(json);
    }

    @Override
    public Result<List<Map<String, Object>>> getAllTypes() {
        String sql = "SELECT DISTINCT type FROM data_dictionary";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list != null) {
            return Result.ok(list);
        } else {
            return Result.fail();
        }
    }
}