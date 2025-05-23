package com.stonebridge.tradeflow.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private String id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String gender;

    private String email;

    private String phone;

    private String avatar;

    private Long departmentId;

    private String jobTitle;

    private Long roleId;

    private String userType;

    private String status;

    private Date entryDate;

    private Date leaveDate;

    private String address;

    private String remark;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;

    @TableLogic
    private Integer isDeleted;
}
