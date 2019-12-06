/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.calculate;

import com.cxytiandi.frame.common.component.SystemConfig;
import com.cxytiandi.frame.util.BaseConstant;
import com.cxytiandi.frame.util.string.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class NumberUtil {
    private static final DecimalFormat format0 = new DecimalFormat("#,###");
    private static final DecimalFormat format2 = new DecimalFormat("#,###.00");
    private static final DecimalFormat format4 = new DecimalFormat("#,###.0000");
    private static final DecimalFormat format2percent = new DecimalFormat("#,###.00%");
    private static final DecimalFormat format4percent = new DecimalFormat("#,###.0000%");


    /**
     * 除法
     *
     * @param dividend
     * @param divisor
     * @return
     */
    public static BigDecimal saveDivide(BigDecimal dividend, BigDecimal divisor) {

        BigDecimal percent = null;
        if (dividend != null && divisor != null
                && divisor.compareTo(BigDecimal.ZERO) != 0)
            percent = dividend.divide(divisor, 6, SystemConfig.ROUNDING_MODE);
        return percent;
    }

    /**
     * 乘法
     *
     * @param multiplicand
     * @param multiplier
     * @return
     */
    public static BigDecimal saveMultiply(BigDecimal multiplicand,
                                          BigDecimal multiplier) {
        BigDecimal g = null;
        if (multiplicand != null && multiplier != null)
            g = multiplicand.multiply(multiplier);
        return g;
    }

    /**
     * 加法
     *
     * @param numbers
     * @return
     */
    public static BigDecimal saveAdd(BigDecimal... numbers) {
        BigDecimal total = null;
        if (numbers != null) {
            BigDecimal abigdecimal[];
            int j = (abigdecimal = numbers).length;
            for (int i = 0; i < j; i++) {
                BigDecimal num = abigdecimal[i];
                if (num != null)
                    if (total == null)
                        total = num;
                    else
                        total = total.add(num);
            }
        }
        return total;
    }

    /**
     * 减法
     *
     * @param minuend
     * @param minus
     * @return
     */
    public static BigDecimal saveSubtract(BigDecimal minuend, BigDecimal minus) {
        if (minuend == null)
            if (minus != null)
                return minus.negate();
            else
                return null;
        if (minus != null)
            return minuend.subtract(minus);
        else
            return minuend;
    }

    public static int compareTo(BigDecimal front, BigDecimal back) {
        if (front == null) {
            front = toBigDecimal(-99999999);
        }
        if (back == null) {
            back = toBigDecimal(-99999999);
        }
        return front.compareTo(back);

    }

    public static String formatCurrency(BigDecimal money, int scale) {
        return formatCurrency(money, scale, SystemConfig.ROUNDING_MODE);
    }

    public static String formatCurrency(BigDecimal money, int scale, RoundingMode roundingMode) {
        String output = null;
        if (money != null) {
            money = money.setScale(scale, roundingMode);
            if (scale == 2)
                output = format2.format(money);
            else if (scale == 4)
                output = format4.format(money);
            else if (scale == 0)
                output = format0.format(money);
            else
                output = (new DecimalFormat(getNumberFormatString(scale).toString())).format(money);
        }
        return output;
    }

    public static BigDecimal decimalToPercent(BigDecimal decimal) {
        return decimalToPercent(decimal, 4);
    }

    public static BigDecimal decimalToPercent(BigDecimal decimal, int scale) {
        BigDecimal percent = null;
        if (decimal != null)
            percent = decimal.multiply(toBigDecimal(100)).setScale(scale, SystemConfig.ROUNDING_MODE);
        return percent;
    }

    public static BigDecimal percentToDecimal(BigDecimal percent) {
        return percentToDecimal(percent, 4);
    }

    public static BigDecimal percentToDecimal(BigDecimal percent, int scale) {
        BigDecimal dec = null;
        if (percent != null)
            dec = percent.divide(toBigDecimal(100), scale, SystemConfig.ROUNDING_MODE);
        return dec;
    }

    public static String formatPercent(BigDecimal percent, int scale) {
        return formatPercent(percent, scale, SystemConfig.ROUNDING_MODE);
    }

    public static String formatPercent(BigDecimal percent, int scale, RoundingMode roundingMode) {
        String output = null;
        if (percent != null) {
            percent = percent.setScale(scale + 2, roundingMode);
            if (scale == 2)
                output = format2percent.format(percent);
            else if (scale == 4)
                output = format4percent.format(percent);
            else
                output = (new DecimalFormat(getNumberFormatString(scale).append("%").toString())).format(percent);
        }
        return output;
    }

    private static StringBuilder getNumberFormatString(int scale) {
        StringBuilder pattern = new StringBuilder("#,##0");
        if (scale > 0) {
            pattern.append(".0");
            for (int i = 1; i < scale; i++)
                pattern.append("0");

        }
        return pattern;
    }

    /**
     * first > second false, first <= second true
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean validateMinMax(BigDecimal first, BigDecimal second) {
        boolean valid = false;
        if (first == null || second == null)
            valid = true;
        else if (first.compareTo(second) <= 0)
            valid = true;
        return valid;
    }

    public static boolean isEmptyOrZero(Number num) {
        return num == null || num.doubleValue() == 0.0D;
    }

    public static boolean isAboveZero(Number num) {
        return num != null && num.doubleValue() > 0.0D;
    }

    public static BigDecimal toBigDecimal(Object o) throws NumberFormatException {
        if (StringUtil.isEmpty(o)) return null;
        if (o instanceof BigDecimal)
            return (BigDecimal) o;
        if (o != null)
            return new BigDecimal(o.toString());
        else
            return null;
    }

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

    public static double toDouble(BigDecimal val) {
        return val == null ? 0 : val.doubleValue();
    }

    public static double toDouble(BigDecimal value, double defaultValue) {
        return value == null ? defaultValue : value.doubleValue();
    }

    public static BigDecimal min(BigDecimal... numbers) {
        BigDecimal min = null;
        if (numbers != null) {
            BigDecimal abigdecimal[];
            int j = (abigdecimal = numbers).length;
            for (int i = 0; i < j; i++) {
                BigDecimal num = abigdecimal[i];
                if (num != null)
                    if (min == null)
                        min = num;
                    else
                        min = min.min(num);
            }
        }
        return min;
    }

    public static BigDecimal max(BigDecimal... numbers) {
        BigDecimal max = null;
        if (numbers != null) {
            BigDecimal abigdecimal[];
            int j = (abigdecimal = numbers).length;
            for (int i = 0; i < j; i++) {
                BigDecimal num = abigdecimal[i];
                if (num != null)
                    if (max == null)
                        max = num;
                    else
                        max = max.max(num);
            }
        }
        return max;
    }

    public static void main(String[] args) {
//        System.out.println(validateMinMax(BigDecimal.ZERO, BigDecimal.ZERO));
        System.out.println(compareTo(toBigDecimal(1), toBigDecimal(-1)));


    }
}
