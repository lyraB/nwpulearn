package com.nwpu.content.model;

/**
 * @author yfh
 * @version 1.0
 * @description
 * @date 2023/2/7
 */

public class Code {
    //审核相关状态
    public static final String Submit_Audit_NO = "202002";
    public static final String Submit_Audit_OK = "202003";
    public static final String Audit_NO = "202001";
    public static final String Audit_OK = "202004";

    //发布相关状态
    public static final String Publish_OK = "203002";
    public static final String Publish_NO = "203001";
    public static final String Publish_OFF = "203003";
    //收费情况
    public static final String Charge_OFF = "201000"; // 免费
    public static final String Charge_OK = "201001"; // 收费
}
