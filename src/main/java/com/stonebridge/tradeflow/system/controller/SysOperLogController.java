package com.stonebridge.tradeflow.system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysOperLogService;

@Tag(name = "System库sys_oper_log表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/sys_oper_log")
public class SysOperLogController {

    private SysOperLogService sysOperLogService;

    @Autowired
    public SysOperLogController(SysOperLogService sysOperLogService) {
        this.sysOperLogService = sysOperLogService;
    }
}
