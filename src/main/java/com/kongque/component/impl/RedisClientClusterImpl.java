package com.kongque.component.impl;

import javax.annotation.Resource;
import com.kongque.component.IRedisClient;
import redis.clients.jedis.JedisCluster;

public class RedisClientClusterImpl implements IRedisClient {
	@Resource
	private JedisCluster jedisCluster;

	@Override
	
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String set(String key, int ex, String value) {
		return jedisCluster.set(key, value);
	}

	/* (non-Javadoc)
	 * @see com.kongque.component.IRedisClient#append(java.lang.String, java.lang.String)
	 */
	@Override
	public Long append(String key, String value) {
		return jedisCluster.append(key, value);
	}
	
	/*
	 * @see com.kongque.component.IRedisClient#remove(java.lang.String)
	 */
	@Override
	public Long remove(String key) {		
		return jedisCluster.del(key);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public Long hset(String key, String item, String value) {
		return jedisCluster.hset(key, item, value);
	}

	@Override
	public String hget(String key, String item) {
		return jedisCluster.hget(key, item);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public Long decr(String key) {
		return jedisCluster.decr(key);
	}

	@Override
	public Long expire(String key, int second) {
		return jedisCluster.expire(key, second);
	}

	@Override
	public Long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public Long hdel(String key, String item) {
		return jedisCluster.hdel(key, item);
	}

}
