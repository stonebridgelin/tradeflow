package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONObject;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.vo.SysRoleQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysRoleService;

import java.util.List;

@Tag(name = "System库sys_role表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }


    /**
     * @param role
     * @return
     * @RequestBody 不能使用get提交方式，必须使用post提交方式；将传递来的json数据封装到对象里面；如果是列表数据，将封装为List数据
     */

    @Operation(summary = "新增角色", description = "创建新的角色信息，包括角色名称、权限码等基本信息",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "角色信息", required = true))
    @PostMapping("/save")
    public Result saveRole(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.ok();
    }


    @Operation(summary = "更新角色信息", description = "根据角色ID更新角色的基本信息和权限配置",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "角色更新信息", required = true))
    @PostMapping("/update")
    public Result update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.ok();
    }


    //http://localhost:8081/admin/system/sysRole/1/2
    @Operation(summary = "获取分页列表", description = "根据用户 ID 获取详细信息")
    @GetMapping("/{page}/{limit}")
    public Result queryRolePage(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable Integer page,

            @Parameter(name = "limit", example = "5", required = true)
            @PathVariable Integer limit,

            @Parameter(name = "roleQueryVo")
            SysRoleQueryVo roleQueryVo) {

        JSONObject jsonObject = sysRoleService.queryRolePage(page, limit, roleQueryVo);
        return Result.ok(jsonObject);
    }

    @Operation(summary = "删除角色", description = "根据id删除对应的角色信息")
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable String id) {
        sysRoleService.removeById(id);
        return Result.ok();
    }

}
