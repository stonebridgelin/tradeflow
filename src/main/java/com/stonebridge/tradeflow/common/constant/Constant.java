package com.stonebridge.tradeflow.common.constant;

public class Constant {

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";


    /**
     * sys_user.id = 1 超级管理员
     */
    public static final String SUPER_ADMIN_ID = "1";

    /**
     * sys_menu.type = 0 OR 1 菜单类型（目录和菜单）
     */
    public static final String TYPE_MENU = "menu";

    /**
     * sys_menu.type = 2 按钮类型<e
     */
    public static final String TYPE_BUTTON = "button";


    /**
     * data_dictionary表中表示供应商销售账号所在银行名称和代码的type
     */
    public static final String DATA_DICTIONARY_BANK_TYPE = "bank_type";

    /**
     * data_dictionary表中表示供应商销售平台名称和代码的type
     */
    public static final String DATA_DICTIONARY_PLATFORM_TYPE = "platform";

    /**
     * data_dictionary表中表示供应商销售类型type
     */
    public static final String DATA_DICTIONARY_SUPPLIER_TYPE = "supplier_type";

    /**
     * 供应商的状态status-1 启用
     */
    public static final Integer SUPPLIER_STATUS_ENABLE = 1;
    /**
     * 供应商的状态status-0 停用
     */
    public static final Integer SUPPLIER_STATUS_DISABLE = 0;
}
