/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.common.component;

import com.ftx.frame.util.properties.PropertiesUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.RoundingMode;

@Component
public class SystemConfig {

    private static PropertiesUtil properties;
    private static final String SYSTEM = "system";

    /**
     * 系统日期时间类型
     */
    public static String DATETIME_FORMAT;
    /**
     * 系统日期类型
     */
    public static String DATE_FORMAT;
    /**
     * 系统时间类型
     */
    public static String TIME_FORMAT;
    public static RoundingMode ROUNDING_MODE;
    public static String CUR_CODE;
    public static String COUNTRY_CODE;
    /**
     * 过期时间
     */
    public static int EXP_TIME;
    /**
     * 每页显示的数目
     */
    public static int ROWS_PER_PAGE;

    public static String MANAGE_EMAIL_ACCOUNT;
    public static String HOST_EMAIL_ACCOUNT;
    public static String HOST_EMAIL_PASSWORD;
    public static String HOST_EMAIL_SENDER;
    public static String HOST_EMAIL_SMTP;
    public static String SYSTEM_NAME;
    @PostConstruct
    public void init() {
        properties = PropertiesUtil.getInstance(SYSTEM);

        DATETIME_FORMAT = properties.getProperty("base.datetime.format");
        DATE_FORMAT = properties.getProperty("base.date.format");
        TIME_FORMAT = properties.getProperty("base.time.format");
        ROUNDING_MODE = RoundingMode.valueOf(RoundingMode.class, properties.getProperty("base.number.roundingMode"));
        ROWS_PER_PAGE = properties.getPropertyAsInt("base.pagination.rowsPerPage");
        CUR_CODE = properties.getProperty("base.currency.code");
        COUNTRY_CODE = properties.getProperty("base.country.code");
        EXP_TIME = properties.getPropertyAsInt("base.expirationTime");
        SYSTEM_NAME = properties.getProperty("base.system.name");

        HOST_EMAIL_ACCOUNT = properties.getProperty("base.host.emailAccount");
        HOST_EMAIL_PASSWORD = properties.getProperty("base.host.emailPassword");
        HOST_EMAIL_SENDER = properties.getProperty("base.host.emailSender");
        HOST_EMAIL_SMTP = properties.getProperty("base.host.emailSMTP");
        MANAGE_EMAIL_ACCOUNT = properties.getProperty("base.manage.emailAccount");
    }

    public static String getProperty(String key) {
        return properties.getProperty(key).trim();
    }

    public static int getPropertyAsInt(String key) {
        return properties.getPropertyAsInt(key);
    }
}
