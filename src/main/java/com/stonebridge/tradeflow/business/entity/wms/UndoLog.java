package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 分布式事务回滚日志表（Seata使用）
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@Data
@TableName("undo_log")
public class UndoLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 回滚日志主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 分支事务ID
     */
    private Long branchId;
    /**
     * 全局事务ID
     */
    private String xid;
    /**
     * 上下文信息
     */
    private String context;
    /**
     * 回滚数据信息
     */
    private byte[] rollbackInfo;
    /**
     * 日志状态（0:正常 1:全局已提交）
     */
    private Integer logStatus;
    /**
     * 日志创建时间
     */
    private Date logCreated;
    /**
     * 日志修改时间
     */
    private Date logModified;
    /**
     * 扩展字段
     */
    private String ext;

}
