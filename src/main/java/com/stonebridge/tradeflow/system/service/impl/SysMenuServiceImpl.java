package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.utils.MenuHelper;
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

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    private SysRoleMenuMapper sysRoleMenuMapper;

    private SysMenuMapper sysMenuMapper;

    private SysUserRoleMapper sysUserRoleMapper;

    public JdbcTemplate systemJdbcTemplate;


    @Autowired
    public SysMenuServiceImpl(SysRoleMenuMapper sysRoleMenuMapper, SysMenuMapper sysMenuMapper,SysUserRoleMapper sysUserRoleMapper, @Qualifier("systemJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.systemJdbcTemplate = jdbcTemplate;
    }


    /**
     * 获取菜单树结构
     *
     * @param userId 用户ID
     * @return 菜单树JSON数组
     */
    public JSONArray getMenuTreeList(String userId) {
        // 验证输入
        if (Objects.isNull(userId) || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        // 查询一级菜单
        List<SysMenu> firstLevelMenuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("type", "0").orderByAsc("sort_value"));

        // 空列表处理
        if (firstLevelMenuList == null || firstLevelMenuList.isEmpty()) {
            return new JSONArray();
        }

        // 转换为JSON数组并添加子菜单
        return new JSONArray(firstLevelMenuList.stream().map(this::buildMenuTreeNode).collect(Collectors.toList()));
    }

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenuList = sysMenuMapper.selectAll();
        if (CollectionUtils.isEmpty(sysMenuList)) return null;
        //构建树形数据
        return MenuHelper.buildTree(sysMenuList);
    }

    @Transactional
    @Override
    public Result updateStatus(String id, String status) {
        String sql = "UPDATE sys_menu set status=? WHERE id=?";
        int row = systemJdbcTemplate.update(sql, StrUtil.trim(status), StrUtil.trim(id));
        if (row == 1) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @Override
    public boolean existChildrenNode(String id) {
        String sql = "SELECT COUNT(1) FROM sys_menu WHERE parent_id=?";
        Integer count = systemJdbcTemplate.queryForObject(sql, Integer.class, StrUtil.trim(id));
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
            String sql = "SELECT id, sort_value FROM sys_menu WHERE sort_value >= ? FOR UPDATE";
            List<Map<String, Object>> list = systemJdbcTemplate.queryForList(sql, sortValue);

            if (list.isEmpty()) {
                // 直接插入
                sysMenuMapper.insert(sysMenu);
            } else {
                // 更新 sort_value >= sysMenu.sortValue 的记录
                sql = "UPDATE sys_menu SET sort_value = sort_value + 1 WHERE sort_value >= ?";
                systemJdbcTemplate.update(sql, sortValue);

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
            } else {
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
                updateSql = "UPDATE sys_menu SET sort_value = sort_value - 1 WHERE sort_value > ? AND sort_value <= ? AND parent_id=?";
                systemJdbcTemplate.update(updateSql, oldSortValue, newSortValue, parentId);
            } else {
                // 新 sortValue < 旧 sortValue，更新 [newSortValue, oldSortValue]
                //  3  < 7    2 [3,4,5,6,7] 8  --> 2 [7,3,4,5,6] 8
                updateSql = "UPDATE sys_menu SET sort_value = sort_value + 1 WHERE sort_value >= ? AND sort_value < ? AND parent_id=?";
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
     * 构建菜单树节点
     *
     * @param menu 菜单实体
     * @return 包含子菜单的JSON对象
     */
    private JSONObject buildMenuTreeNode(SysMenu menu) {
        // 将菜单对象转换为JSONObject
        JSONObject menuObject = new JSONObject(menu);

        // 查询子菜单
        List<SysMenu> subMenus = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("parent_id", menu.getId()).orderByAsc("sort_value"));

        // 添加子菜单（如果存在）
        if (subMenus != null && !subMenus.isEmpty()) {
            menuObject.put("children", new JSONArray(subMenus));
        }
        return menuObject;
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

    //根据userId查询菜单权限值
    @Override
    public  List<String> getUserMenuListByUserId(String userId) {
        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList;
        if (StrUtil.equals(Constant.SUPER_ADMIN_ID, userId)) {
            sysMenuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("status", 1).in("type", "0", "1").orderByAsc("sort_value"));
        } else {
            sysMenuList = this.findMenuListByUserId(userId, Constant.TYPE_MENU);
        }
        //构建树形数据
//        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        //构建路由
        List<String> rights = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            rights.add(sysMenu.getPath());
        }
        return rights;
    }

    /**
     * 根据userId查询按钮权限值
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> getUserPermsListByUserId(String userId) {
        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList;
        if (StrUtil.equals("1", userId)) {
            sysMenuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("status", 1));
        } else {
            sysMenuList = this.findMenuListByUserId(userId, Constant.TYPE_BUTTON);
        }
        //创建返回的集合
        List<String> permissionList = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getType() == 2) {
                permissionList.add(sysMenu.getPerms());
            }
        }
        return permissionList;
    }

    /**
     * 根据 userId 查询用户授权的菜单列表。
     * 1. 从 sys_user_role 表查询用户的所有角色 ID。
     * 2. 从 sys_role_menu 表查询角色对应的所有菜单 ID（去重）。
     * 3. 从 sys_menu 表查询符合条件的菜单列表（状态为启用，类型为菜单或目录，按排序值升序）。
     *
     * @param userId 用户 ID
     * @return 菜单列表（不会返回 null）
     */
    private List<SysMenu> findMenuListByUserId(String userId, String type) {
        // 输入校验
        if (userId == null || userId.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 查询用户的所有角色 ID
        List<String> roleIds = systemJdbcTemplate.queryForList("SELECT role_id FROM sys_user_role WHERE user_id = ?", String.class, userId);

        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查询所有角色对应的菜单 ID（去重）
        String roleIdsPlaceholder = String.join(",", Collections.nCopies(roleIds.size(), "?"));
        String menuIdQuery = String.format("SELECT DISTINCT menu_id FROM sys_role_menu WHERE role_id IN (%s)", roleIdsPlaceholder);
        List<String> menuIds = systemJdbcTemplate.queryForList(menuIdQuery, String.class, roleIds.toArray());

        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查询菜单列表
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1) // 状态：1 表示启用
                .in("id", menuIds) // 菜单 ID 列表
                .orderByAsc("sort_value"); // 按排序值升序

        if (StrUtil.equals(Constant.TYPE_BUTTON, type)) {
            queryWrapper.eq("type", "2"); // 类型：2按钮
        } else if (StrUtil.equals(Constant.TYPE_MENU, type)) {
            queryWrapper.in("type", "0", "1"); // 类型：0 表示目录，1 表示菜单
        } else {
            return Collections.emptyList(); // 无效类型返回空列表
        }
        return sysMenuMapper.selectList(queryWrapper);
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
        // 1. 查询用户的所有角色 ID
        QueryWrapper<SysUserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId).select("role_id");
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(userRoleQuery);
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        // 去重角色 ID
        Set<Long> roleIds = new HashSet<>();
        for (SysUserRole userRole : userRoles) {
            roleIds.add(userRole.getRoleId());
        }

        // 2. 查询所有角色关联的菜单 ID
        QueryWrapper<SysRoleMenu> roleMenuQuery = new QueryWrapper<>();
        roleMenuQuery.in("role_id", roleIds).select("menu_id");
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(roleMenuQuery);
        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        // 去重菜单 ID
        Set<Long> menuIds = new HashSet<>();
        for (SysRoleMenu roleMenu : roleMenus) {
            menuIds.add(Long.valueOf(roleMenu.getMenuId()));
        }

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

//        log.debug("Found permissions: {}", permissions);
        return permissions;
    }

}
