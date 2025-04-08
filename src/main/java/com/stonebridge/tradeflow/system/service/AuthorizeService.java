package com.stonebridge.tradeflow.system.service;

import com.stonebridge.tradeflow.system.entity.dto.LoginRequest;

public interface AuthorizeService {
    String loginCheck(LoginRequest request);
}
