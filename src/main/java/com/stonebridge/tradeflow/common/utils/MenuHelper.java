package com.stonebridge.tradeflow.common.utils;

import com.stonebridge.tradeflow.system.entity.SysMenu;

import java.util.*;

/**
 * 菜单树构建工具类，用于将平铺的菜单列表构建成多级树形结构。
 */
public class MenuHelper {

    /**
     * 构建菜单树，将平铺的菜单列表转换为多级树形结构。
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

        // 创建菜单ID到菜单对象的映射，便于快速查找
        Map<Long, SysMenu> menuMap = new HashMap<>();
        for (SysMenu menu : menuList) {
            if (menu != null && menu.getId() != null) {
                menuMap.put(menu.getId(), menu);
            }
        }

        List<SysMenu> trees = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for (SysMenu menu : menuList) {
            if (menu == null || menu.getParentId() == null) {
                continue; // 跳过无效菜单
            }
            if (menu.getParentId() == 0) {
                buildChildren(menu, menuMap, visited);
                trees.add(menu);
            }
        }
        
        // 对顶级菜单按 sortValue 升序排序
        trees.sort(Comparator.comparingInt(SysMenu::getSortValue));
        
        return trees;
    }

    /**
     * 递归构建子节点，处理多级树结构。
     *
     * @param menu    当前菜单节点
     * @param menuMap 菜单ID到菜单对象的映射
     * @param visited 已访问的菜单ID，用于检测循环引用
     * @throws IllegalArgumentException 如果检测到循环引用
     */
    private static void buildChildren(SysMenu menu, Map<Long, SysMenu> menuMap, Set<Long> visited) {
        if (menu == null || menu.getId() == null || !visited.add(menu.getId())) {
            throw new IllegalArgumentException("Detected circular reference in menu hierarchy at ID: " + menu.getId());
        }

        // 初始化子节点列表
        menu.setChildren(new ArrayList<>());

        // 查找所有直接子节点
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu potentialChild : menuMap.values()) {
            if (potentialChild != null && potentialChild.getParentId() != null &&
                    Objects.equals(menu.getId(), potentialChild.getParentId())) {
                children.add(potentialChild);
            }
        }

        // 按 sortValue 升序排序
        children.sort(Comparator.comparingInt(SysMenu::getSortValue));

        // 将排序后的子节点添加到菜单
        menu.getChildren().addAll(children);

        // 递归构建子树的子节点
        for (SysMenu child : children) {
            buildChildren(child, menuMap, new HashSet<>(visited));
        }
    }
}