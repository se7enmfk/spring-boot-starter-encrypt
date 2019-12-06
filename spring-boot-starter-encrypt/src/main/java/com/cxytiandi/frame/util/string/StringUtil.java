package com.cxytiandi.frame.util.string;

import com.cxytiandi.frame.common.component.SystemConfig;
import com.cxytiandi.frame.util.BaseConstant;
import com.cxytiandi.frame.util.date.DateUtil;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class StringUtil {

    private final static String ZERO = "0";


    /**
     * 预防SQL注入
     *
     * @param str
     * @return
     */
    public static String preventSQLInjection(String str) {
        return isNotEmpty(str) ? str.replaceAll(".*([';]+|(--)+).*", "").replaceAll("%", "\"") : str;
    }

    public static boolean isEmpty(Object str) {
        return StringUtils.isEmpty(str) || BaseConstant.NULL_STRING.equals(str);
    }

    public static boolean isNotEmpty(Object str) {
        return !StringUtils.isEmpty(str) && !BaseConstant.NULL_STRING.equals(str);
    }

    public static String getSubStr(String str, String split, int index) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < index; i++) {
            sb.append(str.substring(0, str.indexOf(split) + 1));
            str = str.substring(str.indexOf(split) + 1, str.length());
        }

        return sb.toString();
    }

    /**
     * string concat连接字符串
     *
     * @param strings
     * @return
     */
    public static String concat(String... strings) {
        StringBuffer sb = new StringBuffer();
        for (String str : strings) {
            sb.append(isNotEmpty(str) ? str : BaseConstant.EMPTY);
        }
        return sb.toString();
    }

    /**
     * 前置补零
     *
     * @param len
     * @param serNo
     * @return
     */
    public static String concatZero(int len, int serNo) {
        StringBuffer sb = new StringBuffer();

        int serNoLength = (serNo + "").toString().length();

        while (serNoLength < len) {
            sb.append(ZERO);
            serNoLength++;
        }

        return sb.append(serNo).toString();
    }

    /**
     * @param val
     * @return
     */
    public static String getString(Object val) {
        if (val == null) return null;
        String value = "";
        if (val instanceof BigDecimal)
            value = ((BigDecimal) val).toString();
        else if (val instanceof Date)
            value = DateUtil.dateToString((Date) val, SystemConfig.DATE_FORMAT);
        else if (val instanceof Timestamp)
            value = DateUtil.timestampToString((Timestamp) val,
                    SystemConfig.DATETIME_FORMAT);
        else
            value = val.toString();
        return value;
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String getCapitalizedStr(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    public static String capitalizeLow(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            char firstChar = str.charAt(0);
            return (new StringBuilder(strLen)).append(Character.toLowerCase(firstChar)).append(str.substring(1)).toString();
        } else {
            return str;
        }
    }

    public static boolean equals(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        return a.equals(b);
    }

    public static String[][] stringToArray(String str) {
        String[] semicolonSplit = str.split(BaseConstant.SEMICOLON);
        String[][] arr = new String[semicolonSplit.length][];
        for (int i = 0; i < semicolonSplit.length; i++) {
            String[] COMMASplit = semicolonSplit[i].split(BaseConstant.COMMA);
            arr[i] = new String[COMMASplit.length];
            for (int j = 0; j < semicolonSplit.length; j++) {
                arr[i][j] = COMMASplit[j];
            }
        }
        return arr;
    }

    public static void main(String[] args) {
        System.out.println(equals(null,""));
//        System.out.println(getSubStr("/adm/asdf", "/", 2));
        // System.out.println(concatzero(new BigDecimal(5), new
        // BigDecimal(219999999)));

        // System.out.println(concat("", "b", "aaa"));

        /*
         * int times = 100000; String s1 = ""; String s2 = ""; StringBuffer s3 =
         * new StringBuffer(""); long a = System.currentTimeMillis(); for (int i
         * = 0; i < times; i++) { s1 = s1 + "a"; } long b =
         * System.currentTimeMillis(); for (int i = 0; i < times; i++) { s2 =
         * s2.concat("a"); } long c = System.currentTimeMillis(); for (int i =
         * 0; i < times; i++) { s3.append("a"); } long d =
         * System.currentTimeMillis(); System.out.print((b - a) + "|" + (c - b)
         * + "|" + (d - c));
         */
    }
}
