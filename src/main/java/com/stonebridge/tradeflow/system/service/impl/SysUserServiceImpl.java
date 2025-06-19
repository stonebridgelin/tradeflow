package com.stonebridge.tradeflow.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.utils.DateUtil;
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
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

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
    public ObjectNode findByPage(Page<SysUser> page, UserQueryVo userQueryVo) {
        ObjectMapper objectMapper = new ObjectMapper();
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

            wrapper.ge(SysUser::getCreateTime, LocalDateTime.parse(userQueryVo.getCreateTimeBegin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StringUtils.hasText(userQueryVo.getCreateTimeEnd())) {
            wrapper.le(SysUser::getCreateTime, LocalDateTime.parse(userQueryVo.getCreateTimeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // 逻辑删除条件
        // 执行分页查询
        IPage<SysUser> userIPage = page(page, wrapper);
        List<SysUser> sysUserList = userIPage.getRecords();
        ArrayNode jsonArray = objectMapper.createArrayNode();
        for (SysUser sysUser : sysUserList) {
            ObjectNode jsonObject = objectMapper.createObjectNode();
            // 先合并 sysUser 所有字段
            ObjectNode userNode = objectMapper.valueToTree(sysUser);
            jsonObject.setAll(userNode);

            // 再补充自定义字段
            if (sysUser.getCreateTime() != null) {
                jsonObject.put("createTime", DateUtil.format(sysUser.getCreateTime(), DateUtil.DEFAULT_DATETIME_PATTERN));
            }
            if (sysUser.getUpdateTime() != null) {
                jsonObject.put("updateTime", DateUtil.format(sysUser.getUpdateTime(), DateUtil.DEFAULT_DATETIME_PATTERN));
            }
            if (StringUtils.hasText(sysUser.getStatus())) {
                jsonObject.put("status", StatusConverter.getStatusDescription(sysUser.getStatus()));
            }
            if (StringUtils.hasText(sysUser.getFirstName()) && StringUtils.hasText(sysUser.getLastName())) {
                jsonObject.put("fullName", sysUser.getFirstName() + " " + sysUser.getLastName());
            }
            jsonArray.add(jsonObject);
        }
        ObjectNode resultObjct = objectMapper.createObjectNode();
        resultObjct.set("data", jsonArray);
        resultObjct.put("total", userIPage.getTotal());

        return resultObjct;
    }

    @Override
    public ObjectNode getAllRoles(Long userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 查询所有的角色数据（id, role_name）
        List<SysRole> sysRoleList = sysRoleMapper.findAllRoles();

        // 查询当前登录用户的角色数据
        List<Long> sysRoles = sysUserRoleMapper.findSysUserRoleByUserId(userId);

        // 构建响应结果数据
        ObjectNode object = objectMapper.createObjectNode();
        object.putPOJO("allRolesList", sysRoleList);
        object.putPOJO("UserRoleIds", sysRoles);
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
