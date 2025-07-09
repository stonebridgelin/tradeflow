package com.stonebridge.tradeflow.business.entity.attribute;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stonebridge.tradeflow.business.typehandler.StringToBigIntTypeHandler;
import lombok.Data;

/**
 * 属性&属性分组关联实体类
 */
@Data
@TableName("pms_attr_attrgroup_relation")
public class AttrAttrgroupRelation {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id", typeHandler = StringToBigIntTypeHandler.class)
    private String id;//主键ID

    @TableField(value = "attr_id", typeHandler = StringToBigIntTypeHandler.class)
    private String attrId;//属性id

    @TableField(value = "attr_group_id", typeHandler = StringToBigIntTypeHandler.class)
    private String attrGroupId;//属性分组id

    @TableField("attr_sort")
    private Integer attrSort;//属性组内排序
}