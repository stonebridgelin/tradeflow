package com.stonebridge.tradeflow.system.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysLoginLogService;

@RestController
@RequestMapping("/api/sys_login_log")
public class SysLoginLogController {

    private SysLoginLogService sysLoginLogService;

    @Autowired
    public SysLoginLogController(SysLoginLogService sysLoginLogService) {
        this.sysLoginLogService = sysLoginLogService;
    }
}
