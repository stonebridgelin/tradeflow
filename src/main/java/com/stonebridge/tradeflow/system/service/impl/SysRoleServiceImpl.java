package com.stonebridge.tradeflow.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stonebridge.tradeflow.common.utils.DateUtil;
import com.stonebridge.tradeflow.system.entity.SysUserRole;
import com.stonebridge.tradeflow.system.mapper.SysRoleMapper;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.mapper.SysUserRoleMapper;
import com.stonebridge.tradeflow.system.service.SysRoleService;
import com.stonebridge.tradeflow.system.entity.vo.SysRoleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public ObjectNode queryRolePage(Integer pageNum, Integer pageSize, String keyWord) {
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
            keyWord = StringUtils.trimWhitespace(keyWord);
            SysRoleQueryVo queryVo = new SysRoleQueryVo();
            queryVo.setKeyWord(keyWord);
            queryVo.setPageSize(pageSize);
            // OFFSET 是从 0 开始，pageNum 转换为偏移量
            queryVo.setPageNum((pageNum - 1) * pageSize);

            // 执行分页查询
            List<SysRole> roleList = sysRoleMapper.selectRolePage(queryVo);

            // 查询总记录数
            Long total = sysRoleMapper.selectCount(new QueryWrapper<SysRole>().like("role_name", keyWord).like("description", keyWord));
            // 转换为 JSON
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonArray = objectMapper.createArrayNode();
            for (SysRole sysRole : roleList) {
                ObjectNode jsonObject = objectMapper.valueToTree(sysRole);
                if (sysRole.getCreateTime() != null) {
                    jsonObject.put("createTime", DateUtil.format(sysRole.getCreateTime(), DateUtil.DEFAULT_DATETIME_PATTERN));
                }
                if (sysRole.getUpdateTime() != null) {
                    jsonObject.put("updateTime", DateUtil.format(sysRole.getUpdateTime(), DateUtil.DEFAULT_DATETIME_PATTERN));
                }
                jsonArray.add(jsonObject);
            }

            // 构造返回结果
            ObjectNode result = objectMapper.createObjectNode();
            result.set("list", jsonArray);
            result.put("total", total);
            return result;

        } catch (Exception e) {
            log.error("Page query failed", e);
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode error = objectMapper.createObjectNode();
            error.put("error", "Query failed: " + e);
            throw new RuntimeException(error.toString());
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
