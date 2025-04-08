package com.stonebridge.tradeflow.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.common.result.Result;
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
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    private SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/findAll")
    @Operation(summary = "获取所有角色信息", description = "获取所有角色信息")
    public List<SysRole> findAll() {
        List<SysRole> roleList = sysRoleService.list();
        return roleList;
    }

    //http://localhost:8081/admin/system/sysRole/1/2
    @Operation(summary = "获取分页列表", description = "根据用户 ID 获取详细信息")
    @GetMapping("/{page}/{limit}")
    public Result index(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable Long page,

            @Parameter(name = "limit", example = "5", required = true)
            @PathVariable Long limit,

            @Parameter(name = "roleQueryVo", required = false)
            SysRoleQueryVo roleQueryVo) {
        Page<SysRole> pageParam = new Page<>(page, limit);
        IPage<SysRole> pageModel = sysRoleService.selectPage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }

    @Operation(summary = "获取角色信息", description = "根据角色的id查询到角色的详细信息")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }

    /**
     * @param role
     * @return
     * @RequestBody 不能使用get提交方式，必须使用post提交方式；将传递来的json数据封装到对象里面；如果是列表数据，将封装为List数据
     */

    @Operation(summary = "新增角色", description = "接收表单的数据将其存储在数据库中")
    @PostMapping("/save")
    public Result save(@RequestBody SysRole role) {
        return sysRoleService.save(role) ? Result.ok() : Result.fail();
    }

    @Operation(summary = "根据id列表删除", description = "根据id列表批量删除角色信息")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        sysRoleService.removeByIds(idList);
        return Result.ok();
    }

    @Operation(summary = "修改角色", description = "根据接收的SysRole对象的id找到该数据库数据，然后将其他数据更新到数据库")
    @PutMapping("/update")
    public Result updateById(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.ok();
    }

    @Operation(summary = "删除角色", description = "根据id删除对应的角色信息")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.ok();
    }

}
