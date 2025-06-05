package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.DataDictionary;

import java.util.List;
import java.util.Map;

public interface DataDictionaryService extends IService<DataDictionary> {

    Result<Object> getDataDictionaryPage(int currentPage, int pageSize, String keyword, String type);

    Result<JSONObject> saveOrUpdateDt(DataDictionary dt);

    Result<List<Map<String, Object>>> getAllTypes();
}
