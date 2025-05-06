package com.stonebridge.tradeflow.system.service;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.system.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    JSONArray getMenuTreeList(String userId);

    List<SysMenu> findNodes();

    Result updateStatus(String id, String status);

    boolean existChildrenNode(String id);

    void saveMenu(SysMenu sysMenu);
}
