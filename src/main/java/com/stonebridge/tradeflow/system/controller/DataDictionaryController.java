package com.stonebridge.tradeflow.system.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;
import com.stonebridge.tradeflow.system.service.DataDictionaryService;
import org.springframework.web.bind.annotation.*;

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
    public Result<JSONObject> saveOrUpdate(@RequestBody DataDictionary dt) {
        return dataDictionaryService.saveOrUpdateDt(dt);
    }


    @DeleteMapping("delete/{id}")
    public Result<Object> deleteData(@PathVariable(value = "id") String id) {
        Boolean result = dataDictionaryService.removeById(id);
        if (result) {
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
        List<DataDictionary> list = myRedisCache.getDataDictionaryByType(StrUtil.trim(type));
        return Result.ok(list);
    }
}
