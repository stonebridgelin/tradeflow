package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.system.entity.dto.LoginDto;

public interface AuthorizeService {
    String loginCheck(LoginDto request);

    JSONObject getUserInfo(String userId);
}
