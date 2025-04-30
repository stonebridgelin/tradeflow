package com.stonebridge.tradeflow.system.service;

import com.stonebridge.tradeflow.system.entity.dto.LoginDto;

public interface AuthorizeService {
    String loginCheck(LoginDto request);
}
