package com.cxytiandi.frame.util.http;

import com.cxytiandi.frame.util.BaseConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

    /**
     * 对象转化成json string
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {

        ObjectMapper mapper = new ObjectMapper();
        String str = BaseConstant.EMPTY;
        try {
            str = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * json string 转化为对象
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> tClass) {

        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = mapper.readValue(json, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
}
