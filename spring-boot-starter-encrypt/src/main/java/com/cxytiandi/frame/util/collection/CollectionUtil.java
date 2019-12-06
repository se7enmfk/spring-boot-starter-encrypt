/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.collection;

import com.ftx.frame.util.calculate.NumberUtil;
import com.ftx.frame.util.object.ObjectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class CollectionUtil {

    /**
     * 去尾差
     *
     * @param coll       collection
     * @param label_name label name
     * @param <T>        T
     */
    public static <T> void dealPercentage(List<T> coll, String label_name) {
        dealPercentage(coll, label_name, BigDecimal.ONE);
    }

    public static <T> void dealPercentage(List<T> coll, String label_name, BigDecimal tot) {
        CollectionUtil.sort(coll, label_name, null);
        int i = 0;
        BigDecimal suplus = tot;
        for (Object oo : coll) {
            Object o = ObjectUtil.invokeGetter(oo, label_name, false);
            if (i < coll.size() - 1) {
                BigDecimal bigDecimal = BigDecimal.ZERO;
                if (o instanceof BigDecimal)
                    bigDecimal = (BigDecimal) o;
                if (o != null)
                    bigDecimal = NumberUtil.toBigDecimal(o);
                suplus = NumberUtil.saveSubtract(suplus, bigDecimal);
            } else {
                if (o instanceof BigDecimal)
                    ObjectUtil.invokeSetter(oo, label_name, suplus);
                if (o instanceof String)
                    ObjectUtil.invokeSetter(oo, label_name, suplus.toString());
            }

            i++;
        }

    }

    public static <T> void sort(List<T> list, final String fieldName, final String sort) {
        Collections.sort(list, new Comparator<T>() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    StringBuilder s = new StringBuilder();

                    s.append("get").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1))
                            .toString();

                    Method m1 = ((T) a).getClass().getMethod(s.toString(), null);
                    Method m2 = ((T) b).getClass().getMethod(s.toString(), null);

                    Object a1 = m1.invoke(((T) a), null);
                    Object b2 = m2.invoke(((T) b), null);

                    if (sort != null && "desc".equals(sort)) {
                        if (a1 instanceof BigDecimal)
                            ret = ((BigDecimal) b2).compareTo((BigDecimal) a1);
                        else
                            ret = (b2.toString()).compareTo(a1.toString());
                    } else {
                        if (a1 instanceof BigDecimal)
                            ret = ((BigDecimal) a1).compareTo((BigDecimal) b2);
                        else
                            ret = (a1.toString()).compareTo(b2.toString());
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return ret;
            }
        });
    }

    public static List<String> getLabelList(Collection<?> coll, String label_name) {
        List<String> list = new ArrayList();
        for (Object o : coll) {
            list.add(ObjectUtil.invokeGetter(o, label_name, false).toString());
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
    public static boolean isEmpty(Object coll) {
        if (coll == null)
            return true;
        if (coll instanceof Collection<?>) {
            return ((Collection<?>) coll).isEmpty();
        } else if (coll instanceof Map) {
            return ((Map) coll).isEmpty();
        }
        return true;
    }

    /**
     * <p>
     * 校验集合是否不为空
     * </p>
     *
     * @param coll
     * @return boolean
     */
    public static boolean isNotEmpty(Object coll) {
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
                Object value = ObjectUtil.invokeGetter(o, attr, false);
                result.add(value);
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

    public static <T> Map<String, List<T>> listToMapList(List<T> list, String fieldName) {
        Map<String, List<T>> tempMap = new HashMap<>();
        if (list == null) {
            return tempMap;
        }

        for (T t : list) {
            String key = (String) ObjectUtil.invokeGetter(t, fieldName, false);
            if (tempMap.containsKey(key)) {
                List<T> ts = tempMap.get(key);
                ts.add(t);
            } else {
                List<T> ts = new ArrayList<>();
                ts.add(t);
                tempMap.put(key, ts);
            }
        }
        return tempMap;
    }
}
