/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.collection;

import com.ftx.frame.util.object.ObjectUtil;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

    public static List<String> getLabelList(Collection<?> coll, String label_name) {
        List<String> list = new ArrayList<>();
        for (Object o : coll) {
            try {
                list.add(ObjectUtil.invokeGetter(o, label_name, false).toString());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * <p>
     * 校验集合是否为空
     * </p>
     *
     * @param coll
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * <p>
     * 校验集合是否不为空
     * </p>
     *
     * @param coll
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * map转list
     *
     * @param map
     * @param list
     * @return
     */
    public static <T> List<T> mapToList(Map<String, T> map, List<T> list) {
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    /**
     * 判断数组不为空
     *
     * @param arr
     * @return
     */
    public static boolean arrayNotEmpty(String[] arr) {
        return arr != null && arr.length > 0;
    }

    public static boolean arrayNotEmpty(int[] arr) {
        return arr != null && arr.length > 0;
    }

    public static boolean arrayNotEmpty(double[] arr) {
        return arr != null && arr.length > 0;
    }

    public static boolean arrayNotEmpty(List<String> arr) {
        return arr != null && arr.size() > 0;
    }

    /**
     * 数组转为字符串，中间接分隔符
     *
     * @param arr
     * @param sep
     * @return
     */
    public static String arrayToString(String[] arr, String sep, boolean wrapQuote) {
        if (arr != null && arr.length > 0) {
            StringBuilder result = new StringBuilder(1024);
            for (int i = 0; i < arr.length; i++) {
                String wrap = (wrapQuote ? "'" : "");
                result.append(wrap + arr[i] + wrap + (i == arr.length - 1 ? "" : sep));
            }
            return result.toString();
        }
        return null;
    }

    public static String arrayToString(List<String> arr, String sep, boolean wrapQuote) {
        if (arr != null && arr.size() > 0) {
            StringBuilder result = new StringBuilder(1024);
            for (int i = 0; i < arr.size(); i++) {
                String wrap = (wrapQuote ? "'" : "");
                result.append(wrap + arr.get(i) + wrap + (i == arr.size() - 1 ? "" : sep));
            }
            return result.toString();
        }
        return null;
    }

    /**
     * 从List中取某一属性，转为数组(String)
     *
     * @param list
     * @param attr
     * @return
     */
    public static List getAttrFromListToArray(List list, String attr) {
        if (CollectionUtil.isNotEmpty(list)) {
            List result = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                try {
                    Object value = ObjectUtil.invokeGetter(o, attr, false);
                    result.add(value);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                    result.add(null);
                }

            }
            return result;
        }
        return null;
    }

    public static String[] objListToStrArr(List list) {
        if (isNotEmpty(list)) {
            return (String[]) list.toArray(new String[list.size()]);
        }
        return null;
    }

    public static BigDecimal[] objListToBigArr(List list) {
        if (isNotEmpty(list)) {
            return (BigDecimal[]) list.toArray(new BigDecimal[list.size()]);
        }
        return null;
    }

    public static Double[] objListToDblArr(List list) {
        if (isNotEmpty(list)) {
            Double[] result = new Double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = Double.parseDouble(list.get(i).toString());
            }
            return result;
        }
        return null;
    }

}
