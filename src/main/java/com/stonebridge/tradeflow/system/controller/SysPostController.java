package com.stonebridge.tradeflow.system.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysPostService;

@RestController
@RequestMapping("/api/sys_post")
public class SysPostController {

    private SysPostService sysPostService;

    @Autowired
    public SysPostController(SysPostService sysPostService) {
        this.sysPostService = sysPostService;
    }

    // 可扩展具体的接口方法，如分页、保存、更新等
}
