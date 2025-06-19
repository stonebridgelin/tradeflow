package com.stonebridge.tradeflow.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.exception.CustomizeException;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.result.ResultCodeEnum;
import com.stonebridge.tradeflow.common.utils.MenuHelper;
import com.stonebridge.tradeflow.security.utils.SecurityContextHolderUtil;
import com.stonebridge.tradeflow.system.entity.SysRoleMenu;
import com.stonebridge.tradeflow.system.entity.SysUserRole;
import com.stonebridge.tradeflow.system.entity.dto.AssginMenuDto;
import com.stonebridge.tradeflow.system.mapper.SysMenuMapper;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import com.stonebridge.tradeflow.system.mapper.SysRoleMenuMapper;
import com.stonebridge.tradeflow.system.mapper.SysUserRoleMapper;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    private final SysRoleMenuMapper sysRoleMenuMapper;

    private final SysMenuMapper sysMenuMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    public final JdbcTemplate systemJdbcTemplate;


    @Autowired
    public SysMenuServiceImpl(SysRoleMenuMapper sysRoleMenuMapper, SysMenuMapper sysMenuMapper, SysUserRoleMapper sysUserRoleMapper, @Qualifier("systemJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.systemJdbcTemplate = jdbcTemplate;
    }


    /**
     * 获取菜单树结构
     *
     * @return 菜单树JSON数组
     */
    // 获取用户的菜单树结构
    public ArrayNode getMenuTreeList() {
        //从spring security的作用域获取用户id
        String userId = SecurityContextHolderUtil.getUserId();
        if (Objects.isNull(userId)) {
            throw new CustomizeException(ResultCodeEnum.LOGIN_AUTH);
        }
        List<String> menuIds = getAuthorizedMenuIds(userId);

        // 获取所有被授权的菜单和按钮
        List<SysMenu> authorizedMenus = sysMenuMapper.selectBatchIds(menuIds);
        if (authorizedMenus == null || authorizedMenus.isEmpty()) {
            return null; // 如果没有授权的菜单或按钮，返回空数组
        }

        // 获取所有相关菜单（包括父目录）
        Set<String> allMenuIds = new HashSet<>(menuIds);
        for (SysMenu menu : authorizedMenus) {
            String parentId = String.valueOf(menu.getParentId());
            while (parentId != null && (!"0".equals(parentId))) {
                allMenuIds.add(parentId); // 将父菜单ID添加到集合中
                SysMenu parentMenu = sysMenuMapper.selectById(parentId);
                if (parentMenu != null) {
                    parentId = String.valueOf(parentMenu.getParentId()); // 继续向上查找父菜单
                } else {
                    break;
                }
            }
        }

        // 获取所有相关菜单的详细信息
        List<SysMenu> sysMenuList = sysMenuMapper.selectBatchIds(new ArrayList<>(allMenuIds));
        if (sysMenuList == null || sysMenuList.isEmpty()) {
            return null; // 如果没有相关菜单，返回空数组
        }

        // 设置 isSelect 属性
        for (SysMenu menu : sysMenuList) {
            if (menu.getType() == 0) {
                // 目录：检查所有子菜单是否都被授权
                List<SysMenu> children = getChildren(menu.getId(), sysMenuList);
                boolean allChildrenAuthorized = !children.isEmpty() && children.stream()
                        .allMatch(child -> menuIds.contains(String.valueOf(child.getId())));
                menu.setSelect(allChildrenAuthorized); // 设置目录的isSelect属性
            } else {
                // 菜单和按钮：如果在授权列表中，则 isSelect = true
                menu.setSelect(menuIds.contains(String.valueOf(menu.getId())));
            }
        }

        // 构建菜单树并转换为 JSON 数组
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(MenuHelper.buildTree(sysMenuList));
    }

    // 辅助方法：获取子菜单
    private List<SysMenu> getChildren(Long parentId, List<SysMenu> allMenus) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(null);
        if (CollectionUtils.isEmpty(sysMenuList)) return null;
        //构建树形数据
        return MenuHelper.buildTree(sysMenuList);
    }

    @Transactional
    @Override
    public Result<Object> updateStatus(String id, String status) {
        String sql = "UPDATE sys_menu set status=? WHERE id=?";
        int row = systemJdbcTemplate.update(sql, StringUtils.trimWhitespace(status), StringUtils.trimWhitespace(id));
        if (row == 1) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @Override
    public boolean existChildrenNode(String id) {
        String sql = "SELECT COUNT(1) FROM sys_menu WHERE parent_id=?";
        Integer count = systemJdbcTemplate.queryForObject(sql, Integer.class, StringUtils.trimWhitespace(id));
        return count > 0;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveMenu(SysMenu sysMenu) {
        try {
            // 验证 sortValue
            Integer sortValue = sysMenu.getSortValue();
            if (sortValue == null) {
                throw new IllegalArgumentException("sort_value cannot be null");
            }

            // 查询 sort_value >= sysMenu.sortValue 的记录（加锁）
            String sql = "SELECT id, sort_value FROM sys_menu WHERE sort_value >= ? AND parent_id = ? FOR UPDATE";
            List<Map<String, Object>> list = systemJdbcTemplate.queryForList(sql, sortValue, sysMenu.getParentId());

            if (list.isEmpty()) {
                // 直接插入
                sysMenuMapper.insert(sysMenu);
            } else {
                // 更新 sort_value >= sysMenu.sortValue 的记录
                sql = "UPDATE sys_menu SET sort_value = sort_value + 1 WHERE sort_value >= ? AND parent_id = ?";
                systemJdbcTemplate.update(sql, sortValue, sysMenu.getParentId());

                // 插入新记录
                sysMenuMapper.insert(sysMenu);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save menu: " + e.getMessage(), e);
        }
    }


    /**
     * 更新菜单，处理 sortValue 变化
     *
     * @param sysMenu      包含更新属性的 SysMenu对象,但是sortValue为旧的
     * @param newSortValue 新的 sortValue
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateMenu(SysMenu sysMenu, Integer newSortValue) {
        if (newSortValue == null || newSortValue <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortValue 必须为正整数");
        }
        Integer oldSortValue = sysMenu.getSortValue();
        Long parentId = sysMenu.getParentId();
        try {
            // 设置新 sortValue
            sysMenu.setSortValue(newSortValue);

            // 锁定受影响的记录
            String lockSql;
            if (oldSortValue < newSortValue) {
                lockSql = "SELECT id FROM sys_menu WHERE sort_value > ? AND sort_value <= ? AND parent_id = ? AND is_deleted = 0 FOR UPDATE";
                systemJdbcTemplate.queryForList(lockSql, oldSortValue, newSortValue, parentId);
            } else if (oldSortValue > newSortValue){
                lockSql = "SELECT id FROM sys_menu WHERE sort_value >= ? AND sort_value < ? AND parent_id = ? AND is_deleted = 0 FOR UPDATE";
                systemJdbcTemplate.queryForList(lockSql, newSortValue, oldSortValue, parentId);
            }

            systemJdbcTemplate.update("DELETE FROM sys_menu WHERE id = ?", sysMenu.getId());
            String updateSql;

            //根据旧排序位置的比较，进行更新
            if (oldSortValue < newSortValue) {
                // 旧 sortValue < 新 sortValue，更新 [oldSortValue, newSortValue]
                // 7  < 10         6 [7,8,9,10] 11  ----> 6 [8,9,10,7] 11
//                sql = "SELECT id, sort_value FROM sys_menu WHERE sort_value > ? AND sort_value <= ? FOR UPDATE";
//                affectedRecords = systemJdbcTemplate.queryForList(sql, oldSortValue, newSortValue);
                updateSql = "UPDATE sys_menu SET sort_value = sort_value - 1 WHERE sort_value > ? AND sort_value <= ? AND parent_id=? AND is_deleted = 0";
                systemJdbcTemplate.update(updateSql, oldSortValue, newSortValue, parentId);
            } else if (oldSortValue > newSortValue) {
                // 新 sortValue < 旧 sortValue，更新 [newSortValue, oldSortValue]
                //  3  < 7    2 [3,4,5,6,7] 8  --> 2 [7,3,4,5,6] 8
                updateSql = "UPDATE sys_menu SET sort_value = sort_value + 1 WHERE sort_value >= ? AND sort_value < ? AND parent_id=? AND is_deleted = 0";
                systemJdbcTemplate.update(updateSql, newSortValue, oldSortValue, parentId);
            }
            sysMenuMapper.insert(sysMenu);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "更新菜单失败，可能存在主键冲突: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean deleteSysMenuById(String id) {
        Integer row = systemJdbcTemplate.update("DELETE FROM sys_menu WHERE id = ?", id);
        return row == 1 ? Boolean.TRUE : Boolean.FALSE;
    }


    /**
     * 构建树型Menu，根据角色ID获取所有的菜单和已经为该角色分配了菜单的组合（如果已经被分配isSelect为true）
     * <en
     *
     * @param roleId ：角色的id
     * @return ：菜单树
     */
    @Override
    public List<SysMenu> getMenusByRoleId(String roleId) {
        //获取所有的菜单 statu=1
        List<SysMenu> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("status", "1"));
        //根据角色的id查询，角色分配过的菜单列表(sys_role_menu)
        List<SysRoleMenu> roleMenuList = sysRoleMenuMapper.selectList(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        //从第二步查询列表中，获取该角色被分配的所有菜单的id(sys_role_menu.menu_id)
        List<String> roleMenuIds = roleMenuList.stream().map(SysRoleMenu::getMenuId).toList();

        //获取所有菜单，该角色被分配的所有菜单的id进行比对，如果已经被分配isSelect为true，否则为false
        for (SysMenu sysMenu : menuList) {
            sysMenu.setSelect(roleMenuIds.contains(String.valueOf(sysMenu.getId())));
        }
        //转化为树性结构，方便显示
        return MenuHelper.buildTree(menuList);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void doAssign(AssginMenuDto assginMenuDto) {
        //删除角色原有的菜单
        sysRoleMenuMapper.delete(new QueryWrapper<SysRoleMenu>().eq("role_id", assginMenuDto.getRoleId()));
        //重新分配菜单
        for (String menuId : assginMenuDto.getMenuIds()) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuDto.getRoleId());
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }


    /**
     * 根据用户id，从数据库查询用户所具有的权限，将查询结果返回给用户
     * userId --> Sys_UserRole.RoleId -->Sys_RoleMenu.MenuId -->Sys_Menu.perms
     *
     * @param userId :用户id
     * @return
     */
    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        List<String> menuIds = getAuthorizedMenuIds(userId.toString());

        // 3. 查询所有菜单的权限标识
        QueryWrapper<SysMenu> menuQuery = new QueryWrapper<>();
        menuQuery.in("id", menuIds).isNotNull("perms").select("perms");
        List<SysMenu> menus = sysMenuMapper.selectList(menuQuery);
        List<String> permissions = new ArrayList<>();
        Set<String> permissionSet = new HashSet<>(); // 去重权限
        for (SysMenu menu : menus) {
            String perms = menu.getPerms();
            if (perms != null && !perms.isEmpty() && !permissionSet.contains(perms)) {
                permissions.add(perms);
                permissionSet.add(perms);
            }
        }

        return permissions;
    }

    /**
     * 根据用户id，从数据库查询用户所具有菜单的权限，将查询结果返回给用户
     * userId --> Sys_UserRole.RoleId -->Sys_RoleMenu.MenuId-->返回menuIds
     *
     * @param userId :用户id
     * @return
     */
    public List<String> getAuthorizedMenuIds(String userId) {
        // 1. 查询用户的所有角色 ID
        QueryWrapper<SysUserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId).select("role_id");
        List<Long> roleIds = sysUserRoleMapper.findSysUserRoleByUserId(Long.valueOf(userId));

        // 2. 查询所有角色关联的菜单 ID
        QueryWrapper<SysRoleMenu> roleMenuQuery = new QueryWrapper<>();
        roleMenuQuery.in("role_id", roleIds).select("menu_id");
        return sysRoleMenuMapper.selectList(roleMenuQuery).stream().map(SysRoleMenu::getMenuId).toList();
    }

    @Override
    public List<Map<String, String>> getAuthorizedMenu(String userId) {
        List<String> menuIds = getAuthorizedMenuIds(userId);
        QueryWrapper<SysMenu> menuQuery = new QueryWrapper<>();
        menuQuery.in("id", menuIds).eq("type", Constant.MENU_TYPE_VALUE_MENU).select("path", "component");
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(menuQuery);
        if (sysMenuList != null && !sysMenuList.isEmpty()) {
            return sysMenuList.stream().map(sysMenu -> {
                String path = sysMenu.getPath();
                Map<String, String> map = new HashMap<>();
                map.put("path", path);
                map.put("component", sysMenu.getComponent());
                map.put("name", path.substring(path.lastIndexOf('/') + 1));
                return map;
            }).toList();
        }
        return List.of();
    }

    @Override
    public List<String> getAuthorizedButton(String userId) {
        List<String> menuIds = getAuthorizedMenuIds(userId);
        QueryWrapper<SysMenu> menuQuery = new QueryWrapper<>();
        menuQuery.in("id", menuIds).eq("type", Constant.MENU_TYPE_VALUE_BUTTON).select("perms");
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(menuQuery);
        if (sysMenuList != null && !sysMenuList.isEmpty()) {
            return sysMenuList.stream().map(SysMenu::getPerms).toList();
        }
        return List.of();
    }

}
