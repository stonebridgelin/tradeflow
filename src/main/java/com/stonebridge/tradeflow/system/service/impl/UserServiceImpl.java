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
import com.stonebridge.tradeflow.common.utils.StatusConverter;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.entity.dto.AssginRoleDto;
import com.stonebridge.tradeflow.system.mapper.SysRoleMapper;
import com.stonebridge.tradeflow.system.mapper.SysUserRoleMapper;
import com.stonebridge.tradeflow.system.mapper.UserMapper;
import com.stonebridge.tradeflow.system.service.UserService;
import com.stonebridge.tradeflow.system.vo.UserQueryVo;
import com.stonebridge.tradeflow.system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private SysRoleMapper sysRoleMapper;

    private SysUserRoleMapper sysUserRoleMapper;


    @Autowired
    public UserServiceImpl(SysRoleMapper sysRoleMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public JSONObject findByPage(Page<User> page, UserQueryVo userQueryVo) {
        // 创建 LambdaQueryWrapper 用于动态构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 关键字模糊查询（name、description、phone）
        if (StringUtils.hasText(userQueryVo.getKeyword())) {
            wrapper.and(w -> w.like(User::getUsername, userQueryVo.getKeyword())
                    .or().like(User::getFirstName, userQueryVo.getKeyword())
                    .or().like(User::getLastName, userQueryVo.getKeyword())
                    .or().like(User::getPhone, userQueryVo.getKeyword()));
        }

        // 时间范围查询
        if (StringUtils.hasText(userQueryVo.getCreateTimeBegin())) {

            wrapper.ge(User::getCreateTime, DateUtil.format(DateUtil.beginOfDay(DateUtil.parse(userQueryVo.getCreateTimeBegin())), DatePattern.NORM_DATETIME_FORMAT));
        }
        if (StringUtils.hasText(userQueryVo.getCreateTimeEnd())) {
            wrapper.le(User::getCreateTime, DateUtil.format(DateUtil.endOfDay(DateUtil.parse(userQueryVo.getCreateTimeEnd())), DatePattern.NORM_DATETIME_FORMAT));
        }

        // 逻辑删除条件
        wrapper.eq(User::getIsDeleted, 0);
        // 执行分页查询
        IPage<User> userIPage = page(page, wrapper);
        List<User> userList = userIPage.getRecords();
        JSONArray jsonArray = new JSONArray();
        for (User user : userList) {
            JSONObject jsonObject = new JSONObject(user);
            if (user.getCreateTime() != null) {
                jsonObject.set("createTime", DateUtil.format(user.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT));
            }
            if (user.getUpdateTime() != null) {
                jsonObject.set("updateTime", DateUtil.format(user.getUpdateTime(), DatePattern.NORM_DATETIME_FORMAT));
            }
            if (StrUtil.isNotBlank(user.getStatus())) {
                jsonObject.set("status", StatusConverter.getStatusDescription(user.getStatus()));
            }
            if (StrUtil.isNotBlank(user.getFirstName()) && StrUtil.isNotBlank(user.getLastName())) {
                jsonObject.set("status", StatusConverter.getStatusDescription(user.getStatus()));
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
}
