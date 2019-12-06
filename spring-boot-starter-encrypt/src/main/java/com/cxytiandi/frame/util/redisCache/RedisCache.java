/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.redisCache;

import com.ftx.frame.common.component.MapperDependencys;
import com.ftx.frame.util.BaseConstant;
import com.ftx.frame.util.redisCache.SerializeUtil;
import com.ftx.frame.util.string.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RedisCache implements Cache {

    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

	/** The ReadWriteLock. */
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private String id;

	private static final int DB_INDEX = 1;
	private final String COMMON_CACHE_KEY = "FINT:";
	private static final String UTF8 = "utf-8";

	public RedisCache(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("ID can not be null.");
		}
		//缓存全部清除
		//clearAll();

		logger.debug(">>>>>>>>>>>>>>>>>>>>>RedisCache:id=" + id);
		this.id = id;
	}

	@Override
	public void putObject(Object key, Object value) {
		Jedis jedis = null;
		try {
//			jedis = JedisSentinelPoolUtil.getIstance().getResource();
			jedis.select(DB_INDEX);

			byte[] keys = getKey(key).getBytes(UTF8);
			jedis.set(keys, SerializeUtil.serialize(value));
			logger.debug(">>>>>>>>>>>>>>>>>>>>>add cache" + this.id);
			// getSize();
		} catch (Exception e) {
			logger.error("JedisSentinelPoolUtil.getIstance() is null......" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object getObject(Object key) {
		Jedis jedis = null;
		Object value = null;
		try {
//			jedis = JedisSentinelPoolUtil.getIstance().getResource();
			jedis.select(DB_INDEX);
			value = SerializeUtil.unserialize(jedis.get(getKey(key).getBytes(UTF8)));
			logger.debug("从缓存中获取-----" + this.id);
			// jedis.del(getKey(key).getBytes(UTF8));
			// getSize();
		} catch (Exception e) {
			logger.error("JedisSentinelPoolUtil.getIstance() is null......" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public Object removeObject(Object key) {
		Jedis jedis = null;
		Object value = null;
		try {
//			jedis = JedisSentinelPoolUtil.getIstance().getResource();
			jedis.select(DB_INDEX);
			value = jedis.del(getKey(key).getBytes(UTF8));
			logger.debug("LRU算法从缓存中移除-----" + this.id);
			// getSize();
		} catch (Exception e) {
			logger.error("JedisSentinelPoolUtil.getIstance() is null......" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public void clear() {
		Jedis jedis = null;
		try {
//			jedis = JedisSentinelPoolUtil.getIstance().getResource();
			jedis.select(DB_INDEX);
			// 如果有删除操作，会影响到整个表中的数据，因此要清空一个mapper的缓存（一个mapper的不同数据操作对应不同的key）
			Set<byte[]> keys = jedis.keys(getKeys(null).getBytes(UTF8));
			logger.debug("出现CUD操作，清空对应Mapper缓存======>" + keys.size());
			for (byte[] key : keys) {
				jedis.del(key);
			}

			// delete release
			Set<String> releaseSet = MapperDependencys.OBSERVERS.get(this.id);
			if (releaseSet != null) {
				for (String releaseId : releaseSet) {
					keys = jedis.keys(getKeys(releaseId).getBytes(UTF8));
					logger.debug("出现CUD操作，清空对应Mapper relase缓存======>"
							+ keys.size());
					for (byte[] key : keys) {
						jedis.del(key);
					}
				}
			}

		} catch (Exception e) {
			logger.error("jedisSentinelPool is null......" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void clearAll() {
		Jedis jedis = null;
		try {
//			jedis = JedisSentinelPoolUtil.getIstance().getResource();
			jedis.flushDB();
			jedis.flushAll();
		} catch (Exception e) {
			logger.error("JedisSentinelPoolUtil.getIstance() is null......" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * 按照一定规则标识key
	 */
	private String getKey(Object key) {
		String keyFormat = StringUtil.concat(getKeyPre(null),
				DigestUtils.md5Hex(String.valueOf(key)));
		//logger.debug(">>>>>>>>>>>>>>>>keyFormat:" + keyFormat);
		//logger.debug(">>>>>>>>>>>>>>>>key:" + key);
		return keyFormat;
	}

	private String getKeys(String id) {
		id = StringUtil.isEmpty(id) ? this.id : id;
		return StringUtil.concat(getKeyPre(id), BaseConstant.STAR);
	}

	/**
	 * redis key规则前缀
	 */
	private String getKeyPre(String id) {

		id = StringUtil.isEmpty(id) ? this.id : id;

		return StringUtil.concat(COMMON_CACHE_KEY, id, BaseConstant.COLON);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public int getSize() {
		Jedis jedis = null;
		int result = 0;
		try {
//			jedis = JedisSentinelPoolUtil.getIstance().getResource();
			jedis.select(DB_INDEX);
			Set<byte[]> keys = jedis.keys(getKeys(null).getBytes(UTF8));
			if (null != keys && !keys.isEmpty()) {
				result = keys.size();
			}
			logger.debug(this.id + "---->>>>总缓存数:" + result);
		} catch (Exception e) {
			logger.error("JedisSentinelPoolUtil.getIstance() is null......" + e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

}
