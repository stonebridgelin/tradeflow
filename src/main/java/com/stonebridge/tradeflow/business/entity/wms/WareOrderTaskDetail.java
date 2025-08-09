package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serializable;

/**
 * 库存工作单明细信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工作单明细主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 商品SKU编号
     */
    private Long skuId;
    /**
     * 商品SKU名称
     */
    private String skuName;
    /**
     * 商品购买数量
     */
    private Integer skuNum;
    /**
     * 关联工作单ID
     */
    private Long taskId;

}
