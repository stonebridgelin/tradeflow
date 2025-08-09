package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存工作单
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Data
@TableName("wms_ware_order_task")
public class WareOrderTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 库存工作单主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 订单ID（关联订单表）
     */
    private Long orderId;
    /**
     * 订单编号
     */
    private String orderSn;
    /**
     * 收货人姓名
     */
    private String consignee;
    /**
     * 收货人联系电话
     */
    private String consigneeTel;
    /**
     * 订单配送地址
     */
    private String deliveryAddress;
    /**
     * 订单备注信息
     */
    private String orderComment;
    /**
     * 付款方式（1:在线付款 2:货到付款）
     */
    private Integer paymentWay;
    /**
     * 工作单任务状态（0:新建 1:已分配 2:工作中 3:已完成 4:已取消）
     */
    private Integer taskStatus;
    /**
     * 订单商品描述信息
     */
    private String orderBody;
    /**
     * 快递物流追踪号
     */
    private String trackingNo;
    /**
     * 工作单创建时间
     */
    private Date createTime;
    /**
     * 执行仓库ID
     */
    private Long wareId;
    /**
     * 工作单备注信息
     */
    private String taskComment;

}
