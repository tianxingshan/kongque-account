package com.kongque.component.impl;

import javax.annotation.Resource;
import com.kongque.component.IRedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClientSingleImpl implements IRedisClient {
	@Resource
	private JedisPool jedisPool;

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.set(key, value);
		jedis.close();
		return result;
	}

	@Override
	public String set(String key,int ex, String value) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.setex(key,ex, value);
		jedis.close();
		return result;
	}

	/* (non-Javadoc)
	 * @see com.kongque.component.IRedisClient#append(java.lang.String, java.lang.String[])
	 */
	@Override
	public Long append(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.append(key, value);
		jedis.close();
		return result;
	}

	/* (non-Javadoc)
	 * @see com.kongque.component.IRedisClient#remove(java.lang.String)
	 */
	@Override
	public Long remove(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.del(key);
		jedis.close();
		return result;
	}

	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get(key);
		jedis.close();
		return result;
	}

	@Override
	public Long hset(String key, String item, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hset(key, item, value);
		jedis.close();
		return result;
	}

	@Override
	public String hget(String key, String item) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.hget(key, item);
		jedis.close();
		return result;
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.incr(key);
		jedis.close();
		return result;
	}

	@Override
	public Long decr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.decr(key);
		jedis.close();
		return result;
	}

	@Override
	public Long expire(String key, int second) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.expire(key, second);
		jedis.close();
		return result;
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.ttl(key);
		jedis.close();
		return result;
	}

	@Override
	public Long hdel(String key, String item) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hdel(key, item);
		jedis.close();
		return result;
	}

}
