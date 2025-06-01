package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.mapper.DataDictionaryMapper;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements CommandLineRunner, DataDictionaryService {

    private DataDictionaryMapper dataDictionaryMapper;

    private RedisTemplate<String, Object> redisTemplate;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DataDictionaryServiceImpl(DataDictionaryMapper dataDictionaryMapper, RedisTemplate<String, Object> redisTemplate, @Qualifier("systemJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.dataDictionaryMapper = dataDictionaryMapper;
        this.redisTemplate = redisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // 查询所有数据
        List<DataDictionary> list = dataDictionaryMapper.selectList(null);

        // 按 type 分组并存入 Redis（用于前端下拉选）
        Map<String, List<DataDictionary>> typeMap = list.stream().collect(Collectors.groupingBy(DataDictionary::getType));
        for (Map.Entry<String, List<DataDictionary>> entry : typeMap.entrySet()) {
            String typeKey = "dict:type:" + entry.getKey();
            List<DataDictionary> typeList = entry.getValue();
            for (DataDictionary dict : typeList) {
                redisTemplate.opsForHash().put(typeKey, dict.getCode(), dict);
            }
        }

        // 按 code 存储唯一数据（用于后端翻译）
        for (DataDictionary dict : list) {
            String type_code_key = "dict:" + dict.getType() + ":" + dict.getCode();
            redisTemplate.opsForValue().set(type_code_key, dict);
        }
    }

    public List<DataDictionary> getByType(String type) {
        String typeKey = "dict:type:" + type;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(typeKey);
        return entries.values().stream()
                .map(obj -> (DataDictionary) obj)
                .collect(Collectors.toList());
    }

    public DataDictionary getByTypeAndCode(String type, String code) {
        String type_code_key = "dict:" + type + ":" + code;
        return (DataDictionary) redisTemplate.opsForValue().get(type_code_key);
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

    public Result<JSONObject> saveOrUpdateDt(DataDictionary dt) {
        Integer id = dt.getId();
        //如果id不为空，则是更新操作，从数据库获取源数据，将新的属性拷贝到源数据，然后在数据库中完成更新操作。
        JSONObject json = new JSONObject();
        json.set("code", "200");
        if (id != null) {
            DataDictionary dataDictionary = dataDictionaryMapper.selectById(id);
            BeanUtils.copyProperties(dt, dataDictionary);
            dataDictionary.setUpdateTime(new Date());
            dataDictionaryMapper.updateById(dataDictionary);
        } else {
            //如果id为空，则是新增操作，先判断数据库中是否已存在相同的type和code，如果存在则返回错误信息，否则执行新增操作。
            DataDictionary dataDictionary = new DataDictionary();
            Integer rows = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM data_dictionary WHERE type=? and code=?", Integer.class, dt.getType(), dt.getCode());
            if (rows > 0) {
                json.set("code", "500");
                json.set("message", "新增的数据已存在相同的type和name");
                return Result.ok(json);
            }
            dt.setCreateTime(new Date());
            dt.setUpdateTime(new Date());
            dataDictionaryMapper.insert(dt);
        }
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