package com.ftx.frame.util.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftx.frame.util.BaseConstant;

import java.io.IOException;

public class JsonUtil {

    public static String toJSON(Object o) {

        ObjectMapper mapper = new ObjectMapper();
        String str = BaseConstant.EMPTY;
        try {
            str = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static <T> T parseObject(String json, Class<T> tClass) {

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
