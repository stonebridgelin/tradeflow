package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONArray;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysMenuService;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {


    private final SysMenuService sysMenuService;

    @Autowired
    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @Operation(summary = "获取首页树形菜单列表", description = "根据用户信息，获取用户权限，最后生成用户的首页树形菜单列表")
    @RequestMapping(value = "menuTreeList", method = RequestMethod.GET)
    public Result<JSONArray> getMenuTreeList(String userId) {

        JSONArray jsonArray = sysMenuService.getMenuTreeList(userId);
        return Result.ok(jsonArray);
    }

}
