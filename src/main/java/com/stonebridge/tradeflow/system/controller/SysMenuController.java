package com.stonebridge.tradeflow.system.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import com.stonebridge.tradeflow.system.entity.dto.AssginMenuDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Tag(name = "菜单管理", description = "菜单的增删改查，为角色授权菜单等功能") // 定义 API 组名称
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {


    private final SysMenuService sysMenuService;

    @Autowired
    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @Operation(summary = "获取首页树形菜单列表", description = "根据用户信息，获取用户权限，最后生成用户的首页侧边栏树形菜单列表")
    @RequestMapping(value = "menuTreeList", method = RequestMethod.GET)
    public Result<JSONArray> getMenuTreeList(String userId) {
        JSONArray jsonArray = sysMenuService.getMenuTreeList(userId);
        return Result.ok(jsonArray);
    }


    @Operation(summary = "获取菜单管理页面的菜单列表", description = "为菜单管理页面的菜单列表获取全部菜单列表")
    @GetMapping("/findNodes")
    public Result<List<SysMenu>> findNodes() {
        List<SysMenu> list = sysMenuService.findNodes();
        return Result.ok(list);
    }

    /**
     * 更新某个menu的status
     *
     * @param id
     * @param status
     * @return
     */
    @Operation(summary = "更新某个menu的status")
    @GetMapping("updateStatus")
    public Result updateStatus(String id, String status) {
        Result result = sysMenuService.updateStatus(id, status);
        return result;
    }

    @Operation(summary = "根据id删除菜单信息", description = "根据id将该菜单从Sys_Menu表删除")
    @DeleteMapping("delete/{id}")
    public Result deleteMenuNodeById(@PathVariable("id") String id) {
        if (sysMenuService.existChildrenNode(id)) {
            return Result.fail().message("该节点存在子节点，不能删除");
        }
        Boolean result = sysMenuService.deleteSysMenuById(id);
        if (!result) {
            return Result.fail().message("删除失败");
        }
        return Result.ok();
    }

    @Operation(summary = "保存Menu信息", description = "新建Menu时，将所有信息保存的Sys_Menu表")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu) {
        sysMenuService.saveMenu(sysMenu);
        return Result.ok();
    }

    /**
     * 根据ID查询菜单信息
     *
     * @param id 菜单ID
     * @return JSON格式的菜单数据
     */
    @Operation(summary = "查询Menu信息", description = "根据菜单的ID，查询该菜单的全部信息")
    @GetMapping("get/{id}")
    public Result<SysMenu> getMenuById(@PathVariable("id") String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "无效的菜单 ID");
        }
        SysMenu sysMenu = sysMenuService.getById(id);
        if (sysMenu == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "菜单不存在");
        }
        JSONConfig config = new JSONConfig();
        config.setIgnoreNullValue(true);
        return Result.ok(sysMenu);
    }

    /**
     * 保存更新后的SysMenu对象
     *
     * @param menuDto :封装了需要进行了修改的菜单的信息
     * @return 更新结果
     */
    @Operation(summary = "保存修改后的Menu信息", description = "当修改Menu信息后，对修改后的信息更新到Sys_Menu表")
    @PostMapping("update")
    public Result update(@RequestBody SysMenu menuDto) {
        if (menuDto == null || menuDto.getId() == null) {
            throw new CustomizeException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        if (menuDto.getName() == null || menuDto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "菜单名称不能为空");
        }

        SysMenu sysMenu = sysMenuService.getById(menuDto.getId());
        if (sysMenu == null || sysMenu.getIsDeleted() == 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "菜单不存在或已删除");
        }

        // 拷贝属性
        sysMenu.setParentId(menuDto.getParentId() != null ? menuDto.getParentId() : 0L);
        sysMenu.setName(menuDto.getName());
        sysMenu.setType(menuDto.getType() != null ? menuDto.getType() : sysMenu.getType());
        sysMenu.setPath(menuDto.getPath());
        sysMenu.setComponent(menuDto.getComponent());
        sysMenu.setPerms(menuDto.getPerms());
        sysMenu.setIcon(menuDto.getIcon());
        sysMenu.setStatus(menuDto.getStatus());

        if (Objects.equals(menuDto.getSortValue(), sysMenu.getSortValue())) {
            //当菜单的位置没有修改时，直接更新保存
            sysMenuService.saveOrUpdate(sysMenu);
        } else {
            //当菜单的位置修改时，需要将受影响的兄弟菜单进行移动处理
            sysMenuService.updateMenu(sysMenu, menuDto.getSortValue());
        }
        return Result.ok();
    }


    @Operation(summary = "根据角色获取菜单", description = "根据角色ID获取所有的菜单和已经为该角色分配了菜单的组合")
    @GetMapping("/toAssign/{roleId}")
    public Result<List<SysMenu>> toAssign(@PathVariable String roleId) {
        List<SysMenu> list = sysMenuService.getMenusByRoleId(roleId);
        return Result.ok(list);
    }


    @Operation(summary = "给角色分配菜单权限", description = "根据前端提交的角色和菜单的对应关系保存到sys_role_menu，完成给角色分配权限")
    @GetMapping("/doAssign")
    public Result doAssign(@RequestBody AssginMenuDto assginMenuDto) {
        sysMenuService.doAssign(assginMenuDto);
        return Result.ok();
    }
}
