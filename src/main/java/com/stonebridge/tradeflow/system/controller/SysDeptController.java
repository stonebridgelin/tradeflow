package com.stonebridge.tradeflow.system.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysDeptService;

@RestController
@RequestMapping("/api/sys_dept")
public class SysDeptController {
    private SysDeptService sysDeptService;

    @Autowired
    public SysDeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }
}
