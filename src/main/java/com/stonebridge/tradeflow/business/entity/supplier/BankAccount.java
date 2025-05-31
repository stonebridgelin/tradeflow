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
    @TableId(type = IdType.AUTO)
    private String id; //银行账户ID
    private String supplierId; //供应商ID
    private String bankCode; //银行名称
    private String accountName; //账户名称
    private String accountNumber; //账号
    private String branchName; //开户行
    private Date createTime; //创建时间
    private Date updateTime; //更新时间
}
