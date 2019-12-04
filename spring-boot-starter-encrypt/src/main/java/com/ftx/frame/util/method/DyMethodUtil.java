/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.method;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

public class DyMethodUtil {

    public static Object invokeMethod(String jexlExp, Map<String, Object> map) {

        JexlEngine jexl = new JexlEngine();


        //jexl.setStrict(true);
        //jexl.setSilent(false);

        Expression e = jexl.createExpression(jexlExp);

        JexlContext jc = new MapContext();

        for (String key : map.keySet()) {

            jc.set(key, map.get(key));

        }

        if (null == e.evaluate(jc)) {

            return "";

        }

        return e.evaluate(jc);

    }

}
