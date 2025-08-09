package com.stonebridge.tradeflow.business.entity.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

import java.io.Serializable;

/**
 * 仓库信息
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
@Data
@TableName("wms_ware_info")
public class WareInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 仓库主键ID
     */
    @TableId
    @TableField(typeHandler = StringToBigIntTypeHandler.class)
    private String id;
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 仓库详细地址
     */
    private String address;
    /**
     * 所在区域编码（省市县编码）
     */
    private String areacode;

}
