package com.stonebridge.tradeflow.business.entity.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 银行账户实体类
 */
@Data
@TableName("pms_bank_account")
public class BankAccount {
    /**
     * 银行账户ID
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 供应商ID
     */
    private String supplierId;

    /**
     * 银行名称
     */
    private String bankCode;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 账号
     */
    private String accountNumber;

    /**
     * 开户行
     */
    private String branchName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
