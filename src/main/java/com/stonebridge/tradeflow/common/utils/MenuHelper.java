package com.stonebridge.tradeflow.common.utils;
import com.stonebridge.tradeflow.system.entity.SysMenu;

import java.util.*;

/**
 * 菜单树构建工具类，用于将平铺的菜单列表构建成树形结构。
 */
public class MenuHelper {

    /**
     * 构建菜单树，将平铺的菜单列表转换为树形结构。
     *
     * @param menuList 菜单列表，包含所有菜单项
     * @return 树形结构的顶级菜单列表（parentId=0 的菜单）
     * @throws IllegalArgumentException 如果输入为空或存在循环引用
     */
    public static List<SysMenu> buildTree(List<SysMenu> menuList) {
        // 输入验证
        if (menuList == null || menuList.isEmpty()) {
            return Collections.emptyList();
        }

        List<SysMenu> trees = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu == null || menu.getParentId() == null) {
                continue; // 跳过无效菜单
            }
            if (menu.getParentId() == 0) {
                trees.add(findChildren(menu, menuList, new HashSet<>()));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点，构建子树。
     *
     * @param menu 当前菜单节点
     * @param menuList 所有菜单列表
     * @param visited 已访问的菜单 ID，用于检测循环引用
     * @return 包含子树的菜单节点
     * @throws IllegalArgumentException 如果检测到循环引用
     */
    private static SysMenu findChildren(SysMenu menu, List<SysMenu> menuList, Set<Long> visited) {
        if (menu == null || menuList == null) {
            return menu;
        }

        // 初始化子节点列表
        menu.setChildren(new ArrayList<>());

        // 检测循环引用
        Long menuId = menu.getId();
        if (menuId != null) {
            if (!visited.add(menuId)) {
                throw new IllegalArgumentException("Detected circular reference in menu hierarchy at ID: " + menuId);
            }
        }

        for (SysMenu child : menuList) {
            if (child == null || child.getParentId() == null || child.getId() == null) {
                continue; // 跳过无效菜单
            }
            if (Objects.equals(menu.getId(), child.getParentId())) {
                menu.getChildren().add(findChildren(child, menuList, new HashSet<>(visited)));
            }
        }

        return menu;
    }
}