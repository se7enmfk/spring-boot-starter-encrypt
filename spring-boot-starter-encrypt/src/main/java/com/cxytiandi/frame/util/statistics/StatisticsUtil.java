/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.statistics;

import com.ftx.frame.util.calculate.NumberUtil;
import com.ftx.frame.util.object.ObjectUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by se7en on 2017/7/13.
 */
public class StatisticsUtil {

    private static final String PRO_CODE = "pro_code";

    /**
     * 混编产品的收益率或者收盘价
     *
     * @param list
     * @param proCodes
     * @param weights
     * @param valueName
     * @param <T>
     * @return
     */
    public static <T> double[] getProReturns(List<T> list, String[] proCodes, Double[] weights, String valueName) {

        Double[][] proAryReturns = new Double[proCodes.length][];
        for (int i = 0; i < proCodes.length; i++) {
            String proCode = proCodes[i];
            List<Double> returnsList = new ArrayList<>();
            for (T p : list) {
                String pro_code = (String) ObjectUtil.invokeGetter(p, PRO_CODE, false);
                if (proCode.equals(pro_code)) {
                    BigDecimal o = (BigDecimal) ObjectUtil.invokeGetter(p, valueName, false);
                    returnsList.add(o.doubleValue());
                }
            }

            proAryReturns[i] = returnsList.toArray(new Double[returnsList.size()]);
        }

        return StatisticsUtil.combineProductReturn(proAryReturns, weights);
    }

    /**
     * @param fdcEntityList
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <T> List<T> getTWRR(List<T> fdcEntityList, String fieldName) {

        try {
            for (int i = 0; i < fdcEntityList.size(); i++) {
                T entity = fdcEntityList.get(i);
                BigDecimal current_value = (BigDecimal) ObjectUtil.invokeGetter(entity, fieldName, false);
                if (i == 0) {
                    ObjectUtil.invokeSetter(entity, fieldName, BigDecimal.ONE);
                } else {
                    T entity1 = fdcEntityList.get(i - 1);
                    BigDecimal prev_value = (BigDecimal) ObjectUtil.invokeGetter(entity1, fieldName, false);
                    ObjectUtil.invokeSetter(entity, fieldName, NumberUtil.saveMultiply(prev_value, NumberUtil.saveAdd(current_value, BigDecimal.ONE)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fdcEntityList;
    }

    private static double[] combineProductReturn(Double[][] var0, Double[] var1) {
        double[] var2 = new double[var0[0].length];
        int var3 = 0;

        for (int var4 = var0.length; var3 < var4; ++var3) {
            int var5 = 0;

            for (int var6 = var0[var3].length; var5 < var6; ++var5) {
                var2[var5] += var0[var3][var5] * var1[var3];
            }
        }

        return var2;
    }
}
