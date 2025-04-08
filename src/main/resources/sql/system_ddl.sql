-- system_db.sys_dept definition

CREATE TABLE `sys_dept`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `name`        varchar(50) NOT NULL DEFAULT '' COMMENT '部门名称',
    `parent_id`   bigint               DEFAULT '0' COMMENT '上级部门id',
    `tree_path`   varchar(255)         DEFAULT ',' COMMENT '树结构',
    `sort_value`  int                  DEFAULT '1' COMMENT '排序',
    `leader`      varchar(20)          DEFAULT NULL COMMENT '负责人',
    `phone`       varchar(11)          DEFAULT NULL COMMENT '电话',
    `status`      tinyint(1)           DEFAULT '1' COMMENT '状态（1正常 0停用）',
    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_time` timestamp   NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  tinyint     NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2018
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = COMPACT COMMENT ='组织机构';


-- system_db.sys_login_log definition

CREATE TABLE `sys_login_log`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `username`    varchar(50)        DEFAULT '' COMMENT '用户账号',
    `ipaddr`      varchar(128)       DEFAULT '' COMMENT '登录IP地址',
    `status`      tinyint(1)         DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    `msg`         varchar(255)       DEFAULT '' COMMENT '提示信息',
    `access_time` datetime           DEFAULT NULL COMMENT '访问时间',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_time` timestamp NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  tinyint   NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb3 COMMENT ='系统访问记录';


-- system_db.sys_menu definition

CREATE TABLE `sys_menu`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '编号',
    `parent_id`   bigint      NOT NULL DEFAULT '0' COMMENT '所属上级',
    `name`        varchar(20) NOT NULL DEFAULT '' COMMENT '名称',
    `type`        tinyint     NOT NULL DEFAULT '0' COMMENT '类型(0:目录,1:菜单,2:按钮)',
    `path`        varchar(100)         DEFAULT NULL COMMENT '路由地址',
    `component`   varchar(100)         DEFAULT NULL COMMENT '组件路径',
    `perms`       varchar(100)         DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(100)         DEFAULT NULL COMMENT '图标',
    `sort_value`  int                  DEFAULT NULL COMMENT '排序',
    `status`      tinyint              DEFAULT NULL COMMENT '状态(0:禁止,1:正常)',
    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint     NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 35
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='菜单表';


-- system_db.sys_oper_log definition

CREATE TABLE `sys_oper_log`
(
    `id`             bigint    NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title`          varchar(50)        DEFAULT '' COMMENT '模块标题',
    `business_type`  varchar(20)        DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         varchar(100)       DEFAULT '' COMMENT '方法名称',
    `request_method` varchar(10)        DEFAULT '' COMMENT '请求方式',
    `operator_type`  varchar(20)        DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name`      varchar(50)        DEFAULT '' COMMENT '操作人员',
    `dept_name`      varchar(50)        DEFAULT '' COMMENT '部门名称',
    `oper_url`       varchar(255)       DEFAULT '' COMMENT '请求URL',
    `oper_ip`        varchar(128)       DEFAULT '' COMMENT '主机地址',
    `oper_param`     varchar(2000)      DEFAULT '' COMMENT '请求参数',
    `json_result`    varchar(2000)      DEFAULT '' COMMENT '返回参数',
    `status`         int                DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
    `error_msg`      varchar(2000)      DEFAULT '' COMMENT '错误消息',
    `oper_time`      datetime           DEFAULT NULL COMMENT '操作时间',
    `create_time`    timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_time`    timestamp NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`     tinyint   NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 64
  DEFAULT CHARSET = utf8mb3 COMMENT ='操作日志记录';


-- system_db.sys_post definition

CREATE TABLE `sys_post`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code`   varchar(64)  NOT NULL COMMENT '岗位编码',
    `name`        varchar(50)  NOT NULL DEFAULT '' COMMENT '岗位名称',
    `description` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
    `status`      tinyint(1)   NOT NULL DEFAULT '1' COMMENT '状态（1正常 0停用）',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_time` timestamp    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  tinyint      NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb3 COMMENT ='岗位信息表';


-- system_db.sys_role definition

CREATE TABLE `sys_role`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '角色id',
    `role_name`   varchar(20) NOT NULL DEFAULT '' COMMENT '角色名称',
    `role_code`   varchar(20)          DEFAULT NULL COMMENT '角色编码',
    `description` varchar(255)         DEFAULT NULL COMMENT '描述',
    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint     NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8mb3 COMMENT ='角色';


-- system_db.sys_role_menu definition

CREATE TABLE `sys_role_menu`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT,
    `role_id`     bigint    NOT NULL DEFAULT '0',
    `menu_id`     bigint    NOT NULL DEFAULT '0',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint   NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb3 COMMENT ='角色菜单';


-- system_db.sys_user definition

CREATE TABLE `sys_user`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '会员id',
    `username`    varchar(20) NOT NULL DEFAULT '' COMMENT '用户名',
    `password`    varchar(32) NOT NULL DEFAULT '' COMMENT '密码',
    `name`        varchar(50)          DEFAULT NULL COMMENT '姓名',
    `phone`       varchar(11)          DEFAULT NULL COMMENT '手机',
    `head_url`    varchar(200)         DEFAULT NULL COMMENT '头像地址',
    `dept_id`     bigint               DEFAULT NULL COMMENT '部门id',
    `post_id`     bigint               DEFAULT NULL COMMENT '岗位id',
    `description` varchar(255)         DEFAULT NULL COMMENT '描述',
    `status`      tinyint              DEFAULT NULL COMMENT '状态（1：正常 0：停用）',
    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint     NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户表';


-- system_db.sys_user_role definition

CREATE TABLE `sys_user_role`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `role_id`     bigint    NOT NULL DEFAULT '0' COMMENT '角色id',
    `user_id`     bigint    NOT NULL DEFAULT '0' COMMENT '用户id',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint   NOT NULL DEFAULT '0' COMMENT '删除标记（0:可用 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_admin_id` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb3 COMMENT ='用户角色';