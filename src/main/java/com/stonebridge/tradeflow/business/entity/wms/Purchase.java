package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-11-17 13:50:10
 */
@Data
@TableName("wms_purchase")
public class Purchase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 采购单主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 采购人员ID
     */
    private Long assigneeId;
    /**
     * 采购人员姓名
     */
    private String assigneeName;
    /**
     * 采购人员联系电话
     */
    private String phone;
    /**
     * 采购单优先级（数值越大优先级越高）
     */
    private Integer priority;
    /**
     * 采购单状态（0:新建 1:已分配 2:已领取 3:已完成 4:有异常）
     */
    private Integer status;
    /**
     * 目标仓库ID
     */
    private Long wareId;
    /**
     * 采购单总金额
     */
    private BigDecimal amount;
    /**
     * 采购单创建时间
     */
    private Date createTime;
    /**
     * 采购单最后更新时间
     */
    private Date updateTime;

}
