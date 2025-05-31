package com.stonebridge.tradeflow.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.system.entity.DataDictionary;

import java.util.List;

public interface DataDictionaryService extends IService<DataDictionary> {
    List<DataDictionary> getByType(String typeKey);

    DataDictionary getByTypeAndCode(String type, String code);
}
