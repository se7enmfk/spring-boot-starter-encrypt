/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.calculate;


import com.cxytiandi.frame.util.object.ObjectUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CalculateUtil {

		public static <V> List<V> listGroupSum(List<V> resultSet, String groupField ,String  valueField, String totalField) {
			List<V> resultList = new ArrayList<V>();

		 /*Map<String, List<V>> tempMap = FpnUtil.turnListToMapList(resultSet, groupField,new TreeMap<String, List<V>>());
				try {
					for (Object key : tempMap.keySet()) {
						BigDecimal total = new BigDecimal(0.0);
						for (Object mapList : tempMap.get(key)) {
							Object colValue = ObjectUtil.invokeGetter(mapList, valueField, false);
							if (colValue instanceof BigDecimal) {
								total = total.add((BigDecimal) colValue);
							}
						}
						for (Object mapList : tempMap.get(key)) {
							ObjectUtil.invokeSetter(mapList, totalField, total);
						}
						resultList.addAll(tempMap.get(key));
					}
				} catch (Exception e) {
					return resultList;
			}*/
			return resultList;
		}

		public static <V> BigDecimal getBigDicemalTotal(List<V> resultSet, String fieldName) {
			BigDecimal total = new BigDecimal(0.0);
			try {
				for (Object obj : resultSet) {
					Object colValue = ObjectUtil.invokeGetter(obj, fieldName, false);
					if (colValue instanceof BigDecimal) {
						total = total.add((BigDecimal) colValue);
					}
				}
			} catch (Exception e) {

			}
			return total;
		}

}
