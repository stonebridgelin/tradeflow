package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.system.mapper.SysRoleMapper;
import com.stonebridge.tradeflow.system.entity.SysRole;
import com.stonebridge.tradeflow.system.service.SysRoleService;
import com.stonebridge.tradeflow.system.vo.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private SysRoleMapper sysRoleMapper;

    @Autowired
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
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

            // 创建分页对象
            Page<SysRole> page = new Page<>(pageNum, pageSize);

            // 构造查询条件
            QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
            if (roleQueryVo != null && roleQueryVo.getRoleName() != null) {
                wrapper.like("role_name", roleQueryVo.getRoleName());
            }

            // 执行分页查询
            IPage<SysRole> sysRoleIPage = sysRoleMapper.selectPage(page, wrapper);
            List<SysRole> list = sysRoleIPage.getRecords();

            // 转换为 JSON 数组
            JSONArray jsonArray = new JSONArray();
            for (SysRole sysRole : list) {
                // 使用 JSONUtil 序列化 SysRole
                JSONObject jsonObject = JSONUtil.parseObj(sysRole);
                // 格式化 createTime
                if (sysRole.getCreateTime() != null) {
                    jsonObject.set("createTime",
                            DateUtil.format(sysRole.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
                }
                jsonArray.add(jsonObject);
            }

            // 构造返回结果
            JSONObject result = new JSONObject();
            result.set("data", jsonArray);
            result.set("total", sysRoleIPage.getTotal());
            result.set("rows", jsonArray); // 修复：rows 表示记录列表
            result.set("current", sysRoleIPage.getCurrent());
            return result;

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.set("error", "Query failed: " + e.getMessage());
            return error;
        }
    }
}
