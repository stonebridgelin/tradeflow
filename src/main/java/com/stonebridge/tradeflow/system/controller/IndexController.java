package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONArray;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "首页功能菜单", description = "首页功能菜单树生成，基础数据加载") // 定义 API 组名称
@RequestMapping("index")
@RestController
@Slf4j
public class IndexController {

    private SysMenuService sysMenuService;

    @Autowired
    public IndexController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @Operation(summary = "获取首页树形菜单列表", description = "根据用户信息，获取用户权限，最后生成用户的首页树形菜单列表")
    @RequestMapping(value = "menuTreeList", method = RequestMethod.GET)
    public Result<JSONArray> getMenuTreeList(String userId) {

        JSONArray jsonArray = sysMenuService.getMenuTreeList(userId);
        return Result.ok(jsonArray);
    }
}
