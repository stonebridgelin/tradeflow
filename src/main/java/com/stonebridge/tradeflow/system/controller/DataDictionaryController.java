package com.stonebridge.tradeflow.system.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("system/dt")
public class DataDictionaryController {
    private final DataDictionaryService dataDictionaryService;

    private final MyRedisCache  myRedisCache;

    public DataDictionaryController(DataDictionaryService dataDictionaryService, MyRedisCache myRedisCache) {
        this.dataDictionaryService = dataDictionaryService;
        this.myRedisCache = myRedisCache;
    }

    @GetMapping("list/{currentPage}/{pageSize}")
    public Result<Object> getAllDataDictionary(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, String keyword, String type) {
        return dataDictionaryService.getDataDictionaryPage(currentPage, pageSize, keyword, type);
    }

    @PostMapping("save")
    public Result<ObjectNode> saveOrUpdate(@RequestBody DataDictionary dt) {
        return dataDictionaryService.saveOrUpdateDt(dt);
    }


    @DeleteMapping("delete/{id}")
    public Result<Object> deleteData(@PathVariable(value = "id") String id) {
        Boolean result = dataDictionaryService.removeById(id);
        if (result) {
            myRedisCache.refreshCache(MyRedisCache.CacheConstants.TYPE_DATA_DICTIONARY);
            return Result.ok();
        } else {
            return Result.fail();
        }
    }


    @GetMapping("getAllTypes")
    public Result<List<Map<String, Object>>> getAllTypes() {
        return dataDictionaryService.getAllTypes();
    }

    @GetMapping("get/{id}")
    public Result<Object> getDtById(@PathVariable(value = "id") String id) {
        if (id == null || id.trim().isEmpty()) {
            return Result.fail().message("无效的ID");
        } else {
            DataDictionary dataDictionary = dataDictionaryService.getById(id);
            if (dataDictionary == null) {
                return Result.fail().message("数据不存在");
            }
            return Result.ok(dataDictionary);
        }
    }
    /**
     * 根据DataDictionary的type从Redis缓存中获取对应DataDiction
     * @return
     */
    @GetMapping("getDdByType")
    public Result<Object> getDdByType(String type) {
        List<DataDictionary> list = myRedisCache.getDataDictionaryByType(StringUtils.trimWhitespace(type));
        if ("transport_status".equals(type)) {
            list.sort(Comparator.comparingInt(item -> {
                String t = item.getCode();
                if (t != null && t.length() > 2) {
                    try {
                        return Integer.parseInt(t.substring(2));
                    } catch (NumberFormatException e) {
                        // 遇到非数字的就放最后
                        return Integer.MAX_VALUE;
                    }
                }
                return Integer.MAX_VALUE;
            }));
        }

        return Result.ok(list);
    }
}
