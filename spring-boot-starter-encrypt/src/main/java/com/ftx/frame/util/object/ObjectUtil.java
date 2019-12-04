/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.object;

import org.apache.commons.beanutils.MethodUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ObjectUtil {

    private static final ConcurrentMap<Object, Object> classFieldsMap = new ConcurrentHashMap<Object, Object>();

    private static final String GET = "get";
    private static final String SET = "set";
    private static final String IS = "is";

    private ObjectUtil() {
    }

    public static <T> void copyObjectValue(Object from, Object to) {
        copyObjectValue(from, to, null);
    }

    /**
     * 不同对象之间的copy行为
     *
     * @param from
     * @param to
     * @param excludes
     */
    public static <T> void copyObjectValue(Object from, Object to, Set<T> excludes) {
        PropertyDescriptor fromPds[] = retrievePropertyDescriptors(from
                .getClass());
        PropertyDescriptor toPds[] = retrievePropertyDescriptors(to.getClass());
        if (excludes == null)
            excludes = Collections.emptySet();
        try {
            PropertyDescriptor apropertydescriptor[];
            int j = (apropertydescriptor = fromPds).length;
            for (int i = 0; i < j; i++) {
                PropertyDescriptor fromPd = apropertydescriptor[i];
                PropertyDescriptor toPd = null;
                if (!excludes.contains(fromPd.getName())
                        && (toPd = getSameNamePropertyDescriptor(fromPd, toPds)) != null) {
                    Method wm = toPd.getWriteMethod();
                    Method rm = fromPd.getReadMethod();
                    if (wm != null && rm != null)
                        wm.invoke(to,
                                new Object[]{rm.invoke(from, new Object[0])});
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PropertyDescriptor getSameNamePropertyDescriptor(PropertyDescriptor fromPd, PropertyDescriptor toPds[]) {
        PropertyDescriptor retPd = null;
        PropertyDescriptor apropertydescriptor[];
        int j = (apropertydescriptor = toPds).length;
        for (int i = 0; i < j; i++) {
            PropertyDescriptor cpd = apropertydescriptor[i];
            if (!cpd.getName().equals(fromPd.getName())
                    || !cpd.getPropertyType().equals(fromPd.getPropertyType()))
                continue;
            retPd = cpd;
            break;
        }

        return retPd;
    }

    public static Object copyInstance(Object src) {
        if (src == null)
            throw new NullPointerException("src cannot be null");
        Object copyInstance = null;
        Class<? extends Object> srcClass = src.getClass();
        PropertyDescriptor props[] = retrievePropertyDescriptors(srcClass);
        try {
            copyInstance = srcClass.newInstance();
            PropertyDescriptor apropertydescriptor[];
            int j = (apropertydescriptor = props).length;
            for (int i = 0; i < j; i++) {
                PropertyDescriptor pd = apropertydescriptor[i];
                Method rm = pd.getReadMethod();
                Method wm = pd.getWriteMethod();
                if (wm != null && rm != null)
                    wm.invoke(copyInstance,
                            new Object[]{rm.invoke(src, new Object[0])});
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return copyInstance;
    }

    public static <T> PropertyDescriptor[] retrievePropertyDescriptors(Class<T> clazz) throws RuntimeException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new RuntimeException((new StringBuilder(
                    "Bean introspection failed: ")).append(e.getMessage())
                    .toString());
        }
        return beanInfo.getPropertyDescriptors();
    }

    public static void flushCache() {
        classFieldsMap.clear();
    }

    public static <T> void flushFromCache(Class<T> clazz)
            throws NullPointerException {
        if (clazz == null) {
            throw new NullPointerException();
        } else {
            classFieldsMap.remove(clazz);
            return;
        }
    }

    /**
     * get value method
     *
     * @param object
     * @param fieldName
     * @param isBoolean true is "is" method, false is "get" method
     * @return
     */
    public static Object invokeGetter(Object object, String fieldName, boolean isBoolean) {
        return invokeWithGetterMethodName(object, makeGetter(fieldName, isBoolean));
    }

    /**
     * set value method
     *
     * @param object
     * @param fieldName
     * @param value
     */
    public static void invokeSetter(Object object, String fieldName, Object value) {
        try {
            MethodUtils.invokeMethod(object, makeSetter(fieldName), value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Object invokeWithGetterMethodName(Object object, String getterMethodName) {
        Object o = null;
        try {
            o = MethodUtils.invokeMethod(object, getterMethodName, null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return o;
    }

    private static String makeSetter(String fieldName) {
        return makeMethod(SET, fieldName);
    }

    private static String makeGetter(String fieldName, boolean isBoolean) {
        return makeMethod(isBoolean ? IS : GET, fieldName);
    }

    private static String makeMethod(String action, String fieldName) {
        StringBuilder s = new StringBuilder();
        return s.append(action).append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();
    }

}