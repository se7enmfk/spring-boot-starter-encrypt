/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.redisCache;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import com.ftx.frame.util.properties.PropertiesUtil;

public class JedisSentinelPoolUtil extends JedisSentinelPool {

	private static Set<String> SENTINELS;
	private static JedisPoolConfig JEDISPOOLCONFIG;
	private static String MASTERNAME;

	static {
		PropertiesUtil p = PropertiesUtil.getInstance("redis");

		MASTERNAME = p.getProperty("redis.masterName");

		SENTINELS = new HashSet<String>();
		SENTINELS.add(p.getProperty("redis.host1"));
		SENTINELS.add(p.getProperty("redis.host2"));

		JEDISPOOLCONFIG = new JedisPoolConfig();
		JEDISPOOLCONFIG.setMaxIdle(2000);
		JEDISPOOLCONFIG.setMaxTotal(20000);
		JEDISPOOLCONFIG.setMinEvictableIdleTimeMillis(300000);
		JEDISPOOLCONFIG.setNumTestsPerEvictionRun(3);
		JEDISPOOLCONFIG.setTimeBetweenEvictionRunsMillis(60000);
		JEDISPOOLCONFIG.setMaxWaitMillis(20000);
		// jedisPoolConfig.setTestOnBorrow(false);
	}

	private JedisSentinelPoolUtil(String masterName, Set<String> sentinels,
			GenericObjectPoolConfig poolConfig) {
		super(masterName, sentinels, poolConfig);
	}

	public static JedisSentinelPoolUtil getIstance() {
		return new JedisSentinelPoolUtil(MASTERNAME, SENTINELS, JEDISPOOLCONFIG);
	}

}
