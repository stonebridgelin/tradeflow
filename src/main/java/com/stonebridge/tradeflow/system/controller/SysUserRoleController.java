package com.stonebridge.tradeflow.system.controller;

import com.stonebridge.tradeflow.system.service.SysUserRoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/sys_user_role")
public class SysUserRoleController {

    private SysUserRoleService sysUserRoleService;

    @Autowired
    public SysUserRoleController(SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }
}
