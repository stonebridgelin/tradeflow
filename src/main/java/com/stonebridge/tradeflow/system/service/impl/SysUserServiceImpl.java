package com.stonebridge.tradeflow.system.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.system.mapper.SysUserMapper;
import com.stonebridge.tradeflow.system.entity.SysUser;
import com.stonebridge.tradeflow.system.service.SysUserService;
import com.stonebridge.tradeflow.system.vo.SysUserQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public JSONObject findByPage(Page<SysUser> page, SysUserQueryVo sysUserDto) {
        // 创建 LambdaQueryWrapper 用于动态构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键字模糊查询（name、description、phone）
        if (StringUtils.hasText(sysUserDto.getKeyword())) {
            wrapper.and(w -> w.like(SysUser::getName, sysUserDto.getKeyword())
                    .or().like(SysUser::getDescription, sysUserDto.getKeyword())
                    .or().like(SysUser::getUsername, sysUserDto.getKeyword())
                    .or().like(SysUser::getPhone, sysUserDto.getKeyword()));
        }

        // 时间范围查询
        if (StringUtils.hasText(sysUserDto.getCreateTimeBegin())) {

            wrapper.ge(SysUser::getCreateTime, DateUtil.format(DateUtil.beginOfDay(DateUtil.parse(sysUserDto.getCreateTimeBegin())), DatePattern.NORM_DATETIME_FORMAT));
        }
        if (StringUtils.hasText(sysUserDto.getCreateTimeEnd())) {
            wrapper.le(SysUser::getCreateTime, DateUtil.format(DateUtil.endOfDay(DateUtil.parse(sysUserDto.getCreateTimeEnd())), DatePattern.NORM_DATETIME_FORMAT));
        }

        // 逻辑删除条件
        wrapper.eq(SysUser::getIsDeleted, 0);
        // 执行分页查询
        IPage<SysUser> sysUserIPage = page(page, wrapper);
        List<SysUser> sysUserList = sysUserIPage.getRecords();
        JSONArray jsonArray = new JSONArray();
        for (SysUser user : sysUserList) {
            JSONObject jsonObject = new JSONObject(user);
            if (user.getCreateTime() != null) {
                jsonObject.set("createTime", DateUtil.format(user.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT));
            }
            if (user.getUpdateTime() != null) {
                jsonObject.set("updateTime", DateUtil.format(user.getUpdateTime(), DatePattern.NORM_DATETIME_FORMAT));
            }
            if (user.getStatus() != null) {
                jsonObject.set("status", user.getStatus() ? "正常" : "停用");
            }
            jsonArray.add(jsonObject);
        }
        JSONObject resultObjct = new JSONObject();
        resultObjct.set("data", jsonArray);
        resultObjct.set("total", sysUserIPage.getTotal());

        return resultObjct;
    }
}
