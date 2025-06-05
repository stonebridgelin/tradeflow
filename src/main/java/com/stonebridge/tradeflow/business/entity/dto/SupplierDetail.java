package com.stonebridge.tradeflow.business.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stonebridge.tradeflow.business.entity.supplier.BankAccount;
import com.stonebridge.tradeflow.business.entity.supplier.PlatformInfo;
import com.stonebridge.tradeflow.business.entity.supplier.Salesperson;
import lombok.Data;

import java.util.List;

@Data
public class SupplierDetail {
    private String id;
    private String supplierNameEn;//供应商的英文名
    private String supplierNameCn;//供应商的中文名
    @JsonProperty("contact_name")
    private String contactName;//供应商主要联系人的名字
    private String contactPhone;//供应商主要联系方式的电话
    private String email;//供应商主要联系人的邮箱
    private String address;//供应商所在地址
    private String supplierType;//供应商的类型
    private List<String> categories;//供应商提供的产品分类
    private List<CategoryNode> categoryNodes; // //供应商提供的产品分类便于展示
    @JsonProperty("BankAccount")
    private List<BankAccount> bankAccount;//供应商提供的银行账号
    @JsonProperty("PlatformInfo")
    private List<PlatformInfo> platformInfo;//供应商的网上平台地址
    @JsonProperty("Salesperson")
    private List<Salesperson> salesperson;//供应商的业务员信息
}