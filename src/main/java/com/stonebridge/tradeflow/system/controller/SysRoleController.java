package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.vo.SysRoleQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysRoleService;


@Tag(name = "角色管理的接口类", description = "完成角色的增删改查操作，对应Sys_Role表")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * @param role 要保存的角色信息的封装
     * @return ：保存的结果
     */
    @Operation(summary = "新增角色", description = "创建新的角色信息，包括角色名称、权限码等基本信息")
    @PostMapping("/save")
    public Result<Object> saveRole(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.ok();
    }


    @Operation(summary = "更新角色信息", description = "根据角色ID更新角色的基本信息和权限配置",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "角色更新信息", required = true))
    @PostMapping("/update")
    public Result<Object> update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.ok();
    }


    //http://localhost:8081/admin/system/sysRole/1/2
    @Operation(summary = "获取角色的分页列表", description = "分页查询角色的信息")
    @GetMapping("/{page}/{limit}")
    public Result<Object> queryRolePage(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable Integer page,

            @Parameter(name = "limit", example = "5", required = true)
            @PathVariable Integer limit,

            @Parameter(name = "roleQueryVo")
            SysRoleQueryVo roleQueryVo) {

        JSONObject jsonObject = sysRoleService.queryRolePage(page, limit, roleQueryVo);
        return Result.ok(jsonObject);
    }

    @Operation(summary = "删除角色", description = "根据id删除Sys_Role里对应的角色信息")
    @DeleteMapping("/delete/{id}")
    public Result<Object> deleteById(@PathVariable String id) {
        sysRoleService.removeById(id);
        return Result.ok();
    }

}
