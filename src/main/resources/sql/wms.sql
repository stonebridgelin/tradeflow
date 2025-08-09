SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `branch_id` BIGINT NOT NULL,
                            `xid` VARCHAR(100) NOT NULL,
                            `context` VARCHAR(128) NOT NULL,
                            `rollback_info` LONGBLOB NOT NULL,
                            `log_status` INT NOT NULL,
                            `log_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `log_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `ext` VARCHAR(100) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for wms_purchase
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `assignee_id` BIGINT DEFAULT NULL,
                                `assignee_name` VARCHAR(255) DEFAULT NULL,
                                `phone` CHAR(13) DEFAULT NULL,
                                `priority` SMALLINT DEFAULT NULL,
                                `status` SMALLINT DEFAULT NULL,
                                `ware_id` BIGINT DEFAULT NULL,
                                `amount` DECIMAL(18,4) DEFAULT NULL,
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购信息';

-- ----------------------------
-- Table structure for wms_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `purchase_id` BIGINT DEFAULT NULL COMMENT '采购单id',
                                       `sku_id` BIGINT DEFAULT NULL COMMENT '采购商品id',
                                       `sku_num` INT DEFAULT NULL COMMENT '采购数量',
                                       `sku_price` DECIMAL(18,4) DEFAULT NULL COMMENT '采购金额',
                                       `ware_id` BIGINT DEFAULT NULL COMMENT '仓库id',
                                       `status` TINYINT DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for wms_ware_info
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `name` VARCHAR(255) DEFAULT NULL COMMENT '仓库名',
                                 `address` VARCHAR(255) DEFAULT NULL COMMENT '仓库地址',
                                 `areacode` VARCHAR(20) DEFAULT NULL COMMENT '区域编码',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='仓库信息';

-- ----------------------------
-- Table structure for wms_ware_order_task
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
                                       `order_id` BIGINT DEFAULT NULL COMMENT 'order_id',
                                       `order_sn` VARCHAR(255) DEFAULT NULL COMMENT 'order_sn',
                                       `consignee` VARCHAR(100) DEFAULT NULL COMMENT '收货人',
                                       `consignee_tel` CHAR(15) DEFAULT NULL COMMENT '收货人电话',
                                       `delivery_address` VARCHAR(500) DEFAULT NULL COMMENT '配送地址',
                                       `order_comment` VARCHAR(200) DEFAULT NULL COMMENT '订单备注',
                                       `payment_way` TINYINT DEFAULT NULL COMMENT '付款方式【1:在线付款 2:货到付款】',
                                       `task_status` TINYINT DEFAULT NULL COMMENT '任务状态',
                                       `order_body` VARCHAR(255) DEFAULT NULL COMMENT '订单描述',
                                       `tracking_no` CHAR(30) DEFAULT NULL COMMENT '物流单号',
                                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'create_time',
                                       `ware_id` BIGINT DEFAULT NULL COMMENT '仓库id',
                                       `task_comment` VARCHAR(500) DEFAULT NULL COMMENT '工作单备注',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存工作单';

-- ----------------------------
-- Table structure for wms_ware_order_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail` (
                                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
                                              `sku_id` BIGINT DEFAULT NULL COMMENT 'sku_id',
                                              `sku_name` VARCHAR(255) DEFAULT NULL COMMENT 'sku_name',
                                              `sku_num` INT DEFAULT NULL COMMENT '购买个数',
                                              `task_id` BIGINT DEFAULT NULL COMMENT '工作单id',
                                              `ware_id` BIGINT DEFAULT NULL COMMENT '仓库id',
                                              `lock_status` TINYINT DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
                                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存工作单';

-- ----------------------------
-- Table structure for wms_ware_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `sku_id` BIGINT DEFAULT NULL COMMENT 'sku_id',
                                `ware_id` BIGINT DEFAULT NULL COMMENT '仓库id',
                                `stock` INT DEFAULT NULL COMMENT '库存数',
                                `sku_name` VARCHAR(200) DEFAULT NULL COMMENT 'sku_name',
                                `stock_locked` INT DEFAULT 0 COMMENT '锁定库存',
                                PRIMARY KEY (`id`),
                                KEY `idx_sku_id` (`sku_id`),
                                KEY `idx_ware_id` (`ware_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品库存';
