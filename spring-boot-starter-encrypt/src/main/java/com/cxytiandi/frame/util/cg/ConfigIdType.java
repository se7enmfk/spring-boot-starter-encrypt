/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.cg;

/**
 * <p>
 * 数据库表ID类型枚举类
 * </p>
 *
 * @author hubin
 * @Date 2016-04-21
 */
public enum ConfigIdType {
	LONG("0", "Long 类型，主键 ID"),
	STRING("1", "String 类型，主键 ID"),;

	/** 主键 */
	private final String key;

	/** 描述 */
	private final String desc;

	ConfigIdType(final String key, final String desc) {
		this.key = key;
		this.desc = desc;
	}

	public String getKey() {
		return this.key;
	}

	public String getDesc() {
		return this.desc;
	}

}
