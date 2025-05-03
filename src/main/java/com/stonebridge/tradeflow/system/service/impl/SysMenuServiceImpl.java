package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.common.result.Result;
import com.stonebridge.tradeflow.common.utils.MenuHelper;
import com.stonebridge.tradeflow.system.mapper.SysMenuMapper;
import com.stonebridge.tradeflow.system.entity.SysMenu;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    private SysMenuMapper sysMenuMapper;

    public JdbcTemplate systemJdbcTemplate;

    @Autowired
    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper, @Qualifier("systemJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.sysMenuMapper = sysMenuMapper;
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
        List<SysMenu> treeList = MenuHelper.buildTree(sysMenuList); //构建树形数据
        return treeList;
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
}
