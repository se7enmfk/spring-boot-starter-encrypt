/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.redisCache;

import org.apache.ibatis.cache.decorators.LoggingCache;

public class LoggingRedisCache extends LoggingCache {
	public LoggingRedisCache(String id) {
		super(new RedisCache(id));
	}
}
