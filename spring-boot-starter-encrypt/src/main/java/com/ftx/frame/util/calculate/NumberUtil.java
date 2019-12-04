/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.calculate;

import com.ftx.frame.common.component.SystemConfig;
import com.ftx.frame.util.string.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class NumberUtil {
    private static final DecimalFormat format0 = new DecimalFormat("#,###");
    private static final DecimalFormat format2 = new DecimalFormat("#,###.00");
    private static final DecimalFormat format4 = new DecimalFormat("#,###.0000");
    private static final DecimalFormat format2percent = new DecimalFormat("#,###.00%");
    private static final DecimalFormat format4percent = new DecimalFormat("#,###.0000%");
    public static final BigDecimal HUNDRED = new BigDecimal(100);

    private NumberUtil() {
    }

    public static boolean isNumber(String src) {
        boolean isNumber = false;
        try {
            Double.parseDouble(src);
            isNumber = true;
        } catch (NumberFormatException numberformatexception) {
        }
        return isNumber;
    }

    public static BigDecimal saveDivide(BigDecimal dividend, BigDecimal divisor) {
        BigDecimal percent = null;
        if (dividend != null && divisor != null
                && divisor.compareTo(BigDecimal.ZERO) != 0)
            percent = dividend.divide(divisor, 6, SystemConfig.ROUNDING_MODE);
        return percent;
    }

    public static BigDecimal saveMultiply(BigDecimal multiplicand,
                                          BigDecimal multiplier) {
        BigDecimal g = null;
        if (multiplicand != null && multiplier != null)
            g = multiplicand.multiply(multiplier);
        return g;
    }

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

    public static String formatCurrency(BigDecimal money, int scale) {
        return formatCurrency(money, scale, SystemConfig.ROUNDING_MODE);
    }

    public static String formatCurrency(BigDecimal money, int scale,
                                        RoundingMode roundingMode) {
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
                output = (new DecimalFormat(getNumberFormatString(scale)
                        .toString())).format(money);
        }
        return output;
    }

    public static BigDecimal decimalToPercent(BigDecimal decimal) {
        BigDecimal percent = null;
        if (decimal != null)
            percent = decimal.multiply(HUNDRED);
        return percent;
    }

    public static BigDecimal decimalToPercent(BigDecimal decimal, int scale) {
        BigDecimal percent = null;
        if (decimal != null)
            percent = decimal.multiply(HUNDRED).setScale(scale,
                    SystemConfig.ROUNDING_MODE);
        return percent;
    }

    public static BigDecimal percentToDecimal(BigDecimal percent) {
        return percentToDecimal(percent, 4);
    }

    public static BigDecimal percentToDecimal(BigDecimal percent, int scale) {
        BigDecimal dec = null;
        if (percent != null)
            dec = percent.divide(HUNDRED, scale, SystemConfig.ROUNDING_MODE);
        return dec;
    }

    public static String formatPercent(BigDecimal percent, int scale) {
        return formatPercent(percent, scale, SystemConfig.ROUNDING_MODE);
    }

    public static String formatPercent(BigDecimal percent, int scale,
                                       RoundingMode roundingMode) {
        String output = null;
        if (percent != null) {
            percent = percent.setScale(scale + 2, roundingMode);
            if (scale == 2)
                output = format2percent.format(percent);
            else if (scale == 4)
                output = format4percent.format(percent);
            else
                output = (new DecimalFormat(getNumberFormatString(scale)
                        .append("%").toString())).format(percent);
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

    public static void main(String[] args) {
        System.out.println(validateMinMax(BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
