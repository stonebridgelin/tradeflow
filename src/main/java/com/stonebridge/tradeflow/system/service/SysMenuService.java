package com.stonebridge.tradeflow.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import com.stonebridge.tradeflow.system.entity.dto.AssginMenuDto;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;
import java.util.Map;

public interface SysMenuService extends IService<SysMenu> {

    ArrayNode getMenuTreeList();

    List<SysMenu> findNodes();

    Result<Object> updateStatus(String id, String status);

    boolean existChildrenNode(String id);

    void saveMenu(SysMenu sysMenu);

    void updateMenu(SysMenu sysMenu,Integer newSortValue);

    Boolean deleteSysMenuById(String id);

    List<SysMenu> getMenusByRoleId(String roleId);

    void doAssign(AssginMenuDto assginMenuDto);

    List<String> getPermissionsByUserId(Long userId);

    List<Map<String, String>> getAuthorizedMenu(String userId);

    List<String> getAuthorizedButton(String userId);
}
