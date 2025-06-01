package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
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

    public DataDictionaryController(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    @GetMapping("list/{currentPage}/{pageSize}")
    public Result<Object> getAllDataDictionary(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, String keyword, String type) {
        return dataDictionaryService.getDataDictionaryPage(currentPage, pageSize,keyword,type);
    }

    @PostMapping("save")
    public Result<JSONObject> save(@RequestBody DataDictionary dt) {
        return dataDictionaryService.saveDt(dt);
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
}
