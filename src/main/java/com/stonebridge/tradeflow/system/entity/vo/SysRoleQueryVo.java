//
//
package com.stonebridge.tradeflow.system.entity.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 角色查询实体
 * </p>
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
public class SysRoleQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String keyWord; // 角色和描述模糊匹配关键词
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页记录数
}

