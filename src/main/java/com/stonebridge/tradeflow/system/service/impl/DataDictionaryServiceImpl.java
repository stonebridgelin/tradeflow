package com.stonebridge.tradeflow.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.mapper.DataDictionaryMapper;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements CommandLineRunner, DataDictionaryService {

    private DataDictionaryMapper dataDictionaryMapper;

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public DataDictionaryServiceImpl(DataDictionaryMapper dataDictionaryMapper, RedisTemplate<String, Object> redisTemplate) {
        this.dataDictionaryMapper = dataDictionaryMapper;
        this.redisTemplate = redisTemplate;
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
}