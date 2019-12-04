/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util;

public class BaseConstant {

    public final static String UTF8 = "UTF-8";
    public final static String ISO88591 = "iso-8859-1";

    public final static String CONTENT_TYPE = "text/html;charset=UTF-8";
    public final static String COMMA = ",";
    public final static String COLON = ":";
    public final static String SPACE = " ";
    public final static String EMPTY = "";
    public final static String MARK = ".";
    public final static String STAR = "*";
    public final static String NULL_STRING = "null";
    public final static String UNDERLINE = "_";
    public final static String BREAKLINE = "~";
    public final static String LINE = "-";
    public final static String SEMICOLON = ";";
    public final static String HASH_SYMBOL = "#";
    public final static String DOUBLE_DOLAR = "$$";

    public final static String STRING_TRUE = "true";
    public final static String STRING_FALSE = "false";

    public final static String YES = "Y";
    public final static String NO = "N";
    public final static String SYSTEM = "system";
    public static final String EXCEL_TYPE = "xlsx";
    public static final String ATTACHMENT = "attachment";

    //区间包含方式
    public final static String INCLUDE_TYPE_LEFT = "LEFT";
    public final static String INCLUDE_TYPE_RIGHT = "RIGHT";
    public final static String INCLUDE_TYPE_BOTH = "BOTH";
    public final static String INCLUDE_TYPE_NONE = "NONE";

    /**
     * mode
     */
    public final static String FORM_MODE_NEW = "N";
    public final static String FORM_MODE_EDIT = "E";
    public final static String FORM_MODE_VIEW = "V";

    /**
     * AUDIT_STATUS
     */
    public final static String AUDIT_STATUS_NA = "NA";   // 未审核
    public final static String AUDIT_STATUS_ADT = "ADT"; // 审核中
    public final static String AUDIT_STATUS_AGR = "AGR"; // 审核通过
    public final static String AUDIT_STATUS_RJC = "RJC"; // 审核驳回

    public final static String YEAR = "Y";
    public final static String MONTH = "M";
    public final static String SEASON = "S";

    public final static String BROWSER_IE = "i";
    public final static String BROWSER_SAFARI = "s";
    public final static String BROWSER_CHROME = "c";

    public final static String SYSTEM_NAME = "system_name_";
    public final static String UUID = "uuid_";
    public final static String DE_USER = "de_user_";
    public final static String USER = "user_";
    public final static String ROLE = "role_";
    public final static String BRAN = "bran_";
    public final static String BRAN_STRSET = "bran_strset_";
    public final static String ROLE_STRSET = "role_strset_";
    public final static String ROLE_TYPE = "role_type_";

    public final static String MENU = "menu_current";
    public final static String COMPANY = "com_";
    public final static String MODE = "mode_";

    //header key value
    public final static String HEADER_FTX_INTERFACE_KEY = "x-ftx-interface";
    public final static String HEADER_FTX_INTERFACE_VALUE = "ftx-interface";

    //base result code
    public final static String CODE_200 = "200";
    public final static String CODE_201 = "201";
    public final static String CODE_202 = "202";
    public final static String CODE_203 = "203";
    public final static String MSG_200 = "success";

    //审核流程
    public final static String ACTIVITY_START = "start"; // 开始节点
    public final static String ACTIVITY_END = "end"; // 结束节点
    public final static String FIRST_PROCESS = "FIRST_PROCESS";
    public final static String SECOND_PROCESS = "SECOND_PROCESS";
    public final static String THIRD_PROCESS = "THIRD_PROCESS";
    public final static String WKF_TASK = "wkfTask";

    public final static String FIRST_NAME = "first_name";
    public final static String SECOND_NAME = "second_name";
    public final static String THIRD_NAME = "third_name";
    public final static String DIAGRAM_TYPE_PROCESS = "process";
    public final static String DIAGRAM_TYPE_TASK = "task";


    public final static String DEPUTY_OPR_TYPE_C = "C";//设置代理
    public final static String DEPUTY_OPR_TYPE_M = "M";//修改代理
    public final static String DEPUTY_OPR_TYPE_S = "S";//终止代理
}
