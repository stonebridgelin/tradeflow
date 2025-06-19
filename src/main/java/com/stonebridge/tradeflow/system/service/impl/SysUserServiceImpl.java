package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.utils.StatusConverter;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.entity.dto.AssginRoleDto;
import com.stonebridge.tradeflow.system.mapper.SysRoleMapper;
import com.stonebridge.tradeflow.system.mapper.SysUserRoleMapper;
import com.stonebridge.tradeflow.system.mapper.SysUserMapper;
import com.stonebridge.tradeflow.system.service.SysMenuService;
import com.stonebridge.tradeflow.system.service.SysUserService;
import com.stonebridge.tradeflow.system.entity.vo.UserQueryVo;
import com.stonebridge.tradeflow.system.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysMenuService sysMenuService;

    private final JdbcTemplate systemJdbcTemplate;
    private final SysUserMapper sysUserMapper;


    @Autowired
    public SysUserServiceImpl(SysRoleMapper sysRoleMapper, SysUserRoleMapper sysUserRoleMapper, SysMenuService sysMenuService, @Qualifier("systemJdbcTemplate") JdbcTemplate systemJdbcTemplate, SysUserMapper sysUserMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.systemJdbcTemplate = systemJdbcTemplate;
        this.sysMenuService = sysMenuService;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public JSONObject findByPage(Page<SysUser> page, UserQueryVo userQueryVo) {
        // 创建 LambdaQueryWrapper 用于动态构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键字模糊查询（name、description、phone）
        if (StringUtils.hasText(userQueryVo.getKeyword())) {
            wrapper.and(w -> w.like(SysUser::getUsername, userQueryVo.getKeyword())
                    .or().like(SysUser::getFirstName, userQueryVo.getKeyword())
                    .or().like(SysUser::getLastName, userQueryVo.getKeyword())
                    .or().like(SysUser::getPhone, userQueryVo.getKeyword()));
        }

        // 时间范围查询
        if (StringUtils.hasText(userQueryVo.getCreateTimeBegin())) {

            wrapper.ge(SysUser::getCreateTime, DateUtil.format(DateUtil.beginOfDay(DateUtil.parse(userQueryVo.getCreateTimeBegin())), DatePattern.NORM_DATETIME_FORMAT));
        }
        if (StringUtils.hasText(userQueryVo.getCreateTimeEnd())) {
            wrapper.le(SysUser::getCreateTime, DateUtil.format(DateUtil.endOfDay(DateUtil.parse(userQueryVo.getCreateTimeEnd())), DatePattern.NORM_DATETIME_FORMAT));
        }

        // 逻辑删除条件
        // 执行分页查询
        IPage<SysUser> userIPage = page(page, wrapper);
        List<SysUser> sysUserList = userIPage.getRecords();
        JSONArray jsonArray = new JSONArray();
        for (SysUser sysUser : sysUserList) {
            JSONObject jsonObject = new JSONObject(sysUser);
            if (sysUser.getCreateTime() != null) {
                jsonObject.set("createTime", DateUtil.format(sysUser.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT));
            }
            if (sysUser.getUpdateTime() != null) {
                jsonObject.set("updateTime", DateUtil.format(sysUser.getUpdateTime(), DatePattern.NORM_DATETIME_FORMAT));
            }
            if (StrUtil.isNotBlank(sysUser.getStatus())) {
                jsonObject.set("status", StatusConverter.getStatusDescription(sysUser.getStatus()));
            }
            if (StrUtil.isNotBlank(sysUser.getFirstName()) && StrUtil.isNotBlank(sysUser.getLastName())) {
                jsonObject.set("fullName", sysUser.getFirstName() + " " + sysUser.getLastName());
            }
            jsonArray.add(jsonObject);
        }
        JSONObject resultObjct = new JSONObject();
        resultObjct.set("data", jsonArray);
        resultObjct.set("total", userIPage.getTotal());

        return resultObjct;
    }

    @Override
    public JSONObject getAllRoles(Long userId) {

        // 查询所有的角色数据（id, role_name）
        List<SysRole> sysRoleList = sysRoleMapper.findAllRoles();

        // 查询当前登录用户的角色数据
        List<Long> sysRoles = sysUserRoleMapper.findSysUserRoleByUserId(userId);

        // 构建响应结果数据
        JSONObject object = new JSONObject();
        object.set("allRolesList", sysRoleList);
        object.set("UserRoleIds", sysRoles);
        return object;
    }

    @Transactional
    public void doAssign(AssginRoleDto assginRoleDto) {

        // 删除之前的所有的用户所对应的角色数据
        sysUserRoleMapper.deleteAllRoleByUserId(assginRoleDto.getUserId());

        // 分配新的角色数据
        List<Long> roleIdList = assginRoleDto.getRoleIds();
        roleIdList.forEach(roleId -> {
            sysUserRoleMapper.doAssign(assginRoleDto.getUserId(), roleId);
        });
    }

    @Override
    public Map<String, Object> getUserById(Long userId) {
        String sql = "SELECT id,username,first_name,last_name,phone,email,status,gender,avatar FROM sys_user WHERE id = ?";
        Map<String, Object> userMap = systemJdbcTemplate.queryForMap(sql, userId);
        // 遍历并替换 null 为 ""
        userMap.replaceAll((k, v) -> v == null ? "" : v);
        return userMap;
    }

    /**
     * 根据用户id获取用户信息（基本信息 菜单权限 按钮权限信息）
     *
     * @param userId : 用户id
     * @return : 用户信息（基本信息 菜单权限 按钮权限信息）
     */
    @Override
    public ObjectNode getUserInfo(String userId) {
        log.info("用户id：{}", userId);
        // 查询用户信息
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser != null) {
            ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 实例
            // 创建 Jackson ObjectNode 用于构建 JSON
            ObjectNode jsonObject = objectMapper.createObjectNode();
            // 设置用户基本信息
            jsonObject.put("id", sysUser.getId());
            jsonObject.put("avatar", sysUser.getAvatar());
            jsonObject.put("username", sysUser.getUsername());
            // 根据用户的id获取该用户被授权的菜单(sys_menu.type=1)的所有菜单数据Map必须包含{name:"",path:"",componet:""}
            List<Map<String, String>> Routes = sysMenuService.getAuthorizedMenu(userId);
            // 根据 userId 查询菜单权限值，菜单权限通过 sys_menu.path 和 src/router/config.js 的 path 匹配
            jsonObject.putPOJO("routes", Routes);
            // 按钮权限数据
            // // 根据用户的id获取该用户被授权的按钮(sys_menu.type=2)的所有按钮数据
            List<String> permsList = sysMenuService.getAuthorizedButton(userId);
            jsonObject.putPOJO("buttonPermissions", permsList);
            return jsonObject; // 返回 JSON 对象
        }
        return null; // 用户不存在时返回 null
    }
}
