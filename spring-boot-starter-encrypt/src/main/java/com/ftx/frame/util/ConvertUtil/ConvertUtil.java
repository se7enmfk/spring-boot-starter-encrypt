/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.ConvertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 格式转换工具类
 * Author: Kakus
 * CreateTime: 2016/12/28 11:31
 */
public class ConvertUtil {

    /**
     * 获取参数map
     * @param key
     * @param value
     * @return
     */
    public static Map<String, Object> getParamMap(String key, Object  value){
        Map<String, Object> map = new HashMap<>();
        map.put(key,value);
        return map;
    }


    /**
     * 获取参数map
     * @param params
     * @return
     */
    public static Map<String, String> getParamMap(String[][] params){

        Map<String, String> map = new HashMap<>();
        for (String[] param : params) {
            if (param != null && param.length ==2){
                map.put(param[0],param[1]);
            }
        }
        return map;
    }


}
