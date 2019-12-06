/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.BigDecimal;

import com.ftx.frame.util.BaseConstant;

import java.math.BigDecimal;

/**
 * Description: BigDecimal类型工具类
 * Author: Kakus
 * CreateTime: 2016/12/14 15:56
 */
public class BigDecimalUtil {

    /**
     * 判断一个数值是否处于区间内
     *
     * @param val         原值
     * @param min         最小值
     * @param max         最大值
     * @param includeType LEFT/RIGHT/BOTH/NONE  包含左值/右值/全部/不含
     * @return
     */
    //传入BigDecimal单值
    public static boolean between(BigDecimal val, BigDecimal min, BigDecimal max, String includeType) {
        if (val == null || min == null || max == null) {
            return false;
        }
        if (BaseConstant.INCLUDE_TYPE_LEFT.equals(includeType)) {
            return min.compareTo(val) <= 0 && max.compareTo(val) > 0;
        } else if (BaseConstant.INCLUDE_TYPE_RIGHT.equals(includeType)) {
            return min.compareTo(val) < 0 && max.compareTo(val) >= 0;
        } else if (BaseConstant.INCLUDE_TYPE_BOTH.equals(includeType)) {
            return min.compareTo(val) <= 0 && max.compareTo(val) >= 0;
        } else if (BaseConstant.INCLUDE_TYPE_NONE.equals(includeType)) {
            return min.compareTo(val) < 0 && max.compareTo(val) > 0;
        }
        return false;
    }

    //传入double单值
    public static boolean between(double val, BigDecimal min, BigDecimal max, String includeType) {
        return between(new BigDecimal(val), min, max, includeType);
    }

    //传入BigDecimal数组, 任一数值符合条件即为true
    public static boolean between(BigDecimal[] values, BigDecimal min, BigDecimal max, String includeType) {
        boolean result = false;
        for (BigDecimal value : values) {
            result = between(value, min, max, includeType) || result;
        }
        return result;
    }

    //传入double数组
    public static boolean between(double[] values, BigDecimal min, BigDecimal max, String includeType) {
        boolean result = false;
        for (double value : values) {
            result = between(new BigDecimal(value), min, max, includeType) || result;
        }
        return result;
    }

    /**
     * 将数值放大100倍
     *
     * @param val
     * @return
     */
    public static BigDecimal toPercent(BigDecimal val) {
        return val == null ? null : val.multiply(new BigDecimal(100));
    }
    public static BigDecimal toPercent(double val) {
        BigDecimal value = new BigDecimal(val);
        return value.multiply(new BigDecimal(100)) ;
    }

    /**
     * 将数值缩小100倍
     * FTX_TODO 保留倍数待确认
     * @param val
     * @return
     */
    public static BigDecimal toDecimal(BigDecimal val) {
        return val == null ? null : val.divide(new BigDecimal(100), 8, BigDecimal.ROUND_HALF_UP);
    }

    public static double toDouble(BigDecimal val) {
        return val == null ? 0 : val.doubleValue();
    }

    public static double toDouble(BigDecimal value,double defaultValue){
        return value == null?defaultValue:value.doubleValue();
    }

    public static BigDecimal clearNull(BigDecimal value){
        return value==null?BigDecimal.ZERO:value;
    }
}
