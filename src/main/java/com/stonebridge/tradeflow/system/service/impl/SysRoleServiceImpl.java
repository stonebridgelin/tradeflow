package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.system.entity.SysUserRole;
import com.stonebridge.tradeflow.system.mapper.SysRoleMapper;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.mapper.SysUserRoleMapper;
import com.stonebridge.tradeflow.system.service.SysRoleService;
import com.stonebridge.tradeflow.system.entity.vo.SysRoleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public SysRoleServiceImpl(SysUserRoleMapper sysUserRoleMapper, SysRoleMapper sysRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    public JSONObject queryRolePage(Integer pageNum, Integer pageSize, SysRoleQueryVo roleQueryVo) {
        try {
            // 参数校验
            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }
            if (pageSize > 100) {
                throw new IllegalArgumentException("pageSize cannot exceed 100");
            }

            // 设置分页参数
            SysRoleQueryVo queryVo = new SysRoleQueryVo();
            queryVo.setRoleName(roleQueryVo != null ? roleQueryVo.getRoleName() : null);
            queryVo.setPageSize(pageSize);
            // OFFSET 是从 0 开始，pageNum 转换为偏移量
            queryVo.setPageNum((pageNum - 1) * pageSize);

            // 执行分页查询
            List<SysRole> roleList = sysRoleMapper.selectRolePage(queryVo);

            // 查询总记录数
            Long total = sysRoleMapper.selectCount(new QueryWrapper<SysRole>()
                    .like(roleQueryVo != null && roleQueryVo.getRoleName() != null, "role_name", StrUtil.trim(roleQueryVo.getRoleName())));
            // 转换为 JSON
            JSONArray jsonArray = new JSONArray();
            for (SysRole sysRole : roleList) {
                JSONObject jsonObject = JSONUtil.parseObj(sysRole);
                if (sysRole.getCreateTime() != null) {
                    jsonObject.set("createTime", DateUtil.format(sysRole.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
                }
                if (sysRole.getUpdateTime() != null) {
                    jsonObject.set("updateTime", DateUtil.format(sysRole.getUpdateTime(), DatePattern.NORM_DATETIME_PATTERN));
                }
                jsonArray.add(jsonObject);
            }

            // 构造返回结果
            JSONObject result = new JSONObject();
            result.set("data", jsonArray);
            result.set("total", total);
            result.set("rows", jsonArray);
            result.set("current", pageNum);
            return result;

        } catch (Exception e) {
            log.error("Page query failed", e);
            JSONObject error = new JSONObject();
            error.set("error", "Query failed: " + e);
            return error;
        }
    }

    @Override
    public void deleteById(Long roleId) {
        sysRoleMapper.deleteById(roleId);
    }

    /**
     * 根据用户 ID 查询用户的所有角色代码
     *
     * @param userId 用户 ID
     * @return 角色代码列表（role_code，如 ["ROLE_ADMIN", "ROLE_USER"]）
     */
    public List<String> getRoleCodesByUserId(Long userId) {
        log.debug("Querying role codes for userId: {}", userId);

        // 查询用户的所有角色 ID
        QueryWrapper<SysUserRole> roleQuery = new QueryWrapper<SysUserRole>().eq("user_id", userId).select("role_id");
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(roleQuery);
        if (userRoles.isEmpty()) {
            log.warn("No roles found for userId: {}", userId);
            return new ArrayList<>();
        }

        // 查询角色代码
        List<String> roleCodes = new ArrayList<>();
        for (SysUserRole userRole : userRoles) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            if (role != null && role.getRoleCode() != null && !role.getRoleCode().isEmpty()) {
                roleCodes.add(role.getRoleCode());
            }
        }

        log.debug("Found role codes: {}", roleCodes);
        return roleCodes;
    }

}
