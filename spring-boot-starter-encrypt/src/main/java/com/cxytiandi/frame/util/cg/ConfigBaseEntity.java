/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.cg;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 基础实体配置
 * </p>
 *
 * @author hubin
 * @Date 2016-09-22
 */
public class ConfigBaseEntity {
	/*
	 * 包名，不设置默认使用 config.getEntityPackage() 配置内容
	 */
	private String packageName;

	/*
	 * 类名，默认 BaseEntity
	 */
	private String className = "BaseEntity";

	/*
	 * 公共字段数组
	 */
	private String[] columns;

	/**
	 * <p>
	 * 判断是否为公共字段
	 * </p>
	 *
	 * @param column
	 *            判断字段内容
	 * @return
	 */
	public boolean includeColumns(String column) {
		if (StringUtils.isNotEmpty(column)) {
			for (String cl : columns) {
				if (column.equals(cl)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getPackageName() {
		if (StringUtils.isNotEmpty(packageName)) {
			return packageName + "." + className;
		}
		return null;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

}
