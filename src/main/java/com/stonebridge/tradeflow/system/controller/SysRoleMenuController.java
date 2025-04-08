package com.stonebridge.tradeflow.system.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysRoleMenuService;

@RestController
@RequestMapping("/api/sys_role_menu")
public class SysRoleMenuController {

    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    public SysRoleMenuController(SysRoleMenuService sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }
}
