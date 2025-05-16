package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import com.stonebridge.tradeflow.system.entity.dto.AssginMenuDto;
import com.stonebridge.tradeflow.system.entity.vo.RouterVo;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    JSONArray getMenuTreeList(String userId);

    List<SysMenu> findNodes();

    Result updateStatus(String id, String status);

    boolean existChildrenNode(String id);

    void saveMenu(SysMenu sysMenu);

    void updateMenu(SysMenu sysMenu,Integer newSortValue);

    Boolean deleteSysMenuById(String id);

    List<SysMenu> getMenusByRoleId(String roleId);

    void doAssign(AssginMenuDto assginMenuDto);

    List<String> getUserMenuListByUserId(String id);

    List<String> getUserPermsListByUserId(String id);
}
