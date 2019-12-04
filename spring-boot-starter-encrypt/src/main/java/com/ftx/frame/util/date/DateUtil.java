/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.date;

import com.ftx.frame.common.component.SystemConfig;
import com.ftx.frame.util.string.StringUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * date工具类
 *
 * @author se7en zhou
 * @since 2017/11/29 10:34
 */
public class DateUtil {

    public final static String DATE_YYYY_MM_DD_HHMMSS = "yyyy/MM/dd HH:mm:ss";
    public final static String DATE_YYYY_MM_DD = "yyyy-MM-dd";
    public final static String DATE_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public final static String DATE_YYYY_MM = "yyyy/MM";
    public final static String DATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String DATE_YYYYMMDD_HHMMSS = "yyyyMMdd HHmmss";
    public final static String DATE_YYYYMMDD = "yyyyMMdd";
    public final static String DATE_YYYYMM = "yyyyMM";
    public final static String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    public static String dateToString(Date date) {
        return dateToString(date, SystemConfig.DATE_FORMAT, Locale.CHINESE);
    }

    public static String dateToString(Date date, String format) {
        return dateToString(date, format, Locale.CHINESE);
    }

    /**
     * Locale : ENGLISH , CHINESE and so on
     */
    public static String dateToString(Date date, String format, Locale locale) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return dateFormat.format(date);
    }

    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, SystemConfig.DATE_FORMAT);
    }

    public static Date stringToDate(String dateStr, String format) {
        if (dateStr == null)
            return null;
        String trimmedDate = dateStr.trim();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(trimmedDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Timestamp stringToTimestamp(String timestampStr) {
        return stringToTimestamp(timestampStr, SystemConfig.DATETIME_FORMAT);
    }

    public static Timestamp stringToTimestamp(String timestampStr, String format) {
        if (timestampStr == null)
            return null;
        String trimmedTimestamp = timestampStr.trim();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(trimmedTimestamp);
            return new Timestamp(d.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return timestampToString(timestamp, SystemConfig.DATETIME_FORMAT);
    }

    public static String timestampToString(Timestamp timestamp, String format) {
        if (timestamp == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(timestamp.getTime());
        return dateFormat.format(date);
    }

    public static Date sDateToUDate(java.sql.Date sqlDate) {
        if (sqlDate == null)
            return null;

        Date utilDate = new Date(sqlDate.getTime());
        return utilDate;

    }

    public static java.sql.Date uDateToSDate(Date utilDate) {
        if (utilDate == null)
            return null;
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return sqlDate;
    }

    public static Date addDate(Date date, int day) {
        return addDate(date, day, 0, 0);
    }

    public static Date addDate(Date date, int day, int month, int year) {
        Calendar calendar = GregorianCalendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        calendar.add(Calendar.DAY_OF_MONTH, day);
        if (month != 0)
            calendar.add(Calendar.MONTH, month);
        if (year != 0)
            calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static String getCurrDateStr() {
        return dateToString(new Date(), DATE_YYYY_MM_DD);
    }

    public static String getCurrDateTimeStr() {
        return dateToString(new Date(), DATE_YYYY_MM_DD_HH_MM_SS);
    }

    public static Date toDateRange(String rangeType, Date date) {

        if (StringUtil.isNotEmpty(rangeType)) {
            Calendar now = Calendar.getInstance();
            int rangeMonth = getRangeMonth(rangeType);
            now.add(Calendar.MONTH, -rangeMonth);
            return now.getTime();
        } else {
            return date;
        }
    }

    public static int getRangeMonth(String rangeType) {
        int month = 0;
        if (StringUtil.isNotEmpty(rangeType) && rangeType.endsWith("Y")) {
            month = Integer.parseInt(rangeType.replace("Y", "")) * 12;

        } else if (StringUtil.isNotEmpty(rangeType) && rangeType.endsWith("M")) {
            month = Integer.parseInt(rangeType.replace("M", ""));

        }
        return month;
    }

}
