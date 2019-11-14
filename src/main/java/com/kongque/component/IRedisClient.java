package com.kongque.component;

public interface IRedisClient {
	
	/**
	 * 成功返回OK
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(String key, String value);

	/**
	 * 设置 值和过期时间
	 * @param key
	 * @param ex
	 * @param value
	 * @return
	 */
	public String set(String key,int ex, String value);

	public String get(String key);
	
	/**
	 * 向保存在Redis中参数key对应的数据信息中追加单个数据
	 * 
	 * @author pengcheng
	 * @since 2017年10月20日
	 * @param key
	 * @return Long 执行结果状态码——1：表示执行成功
	 */
	public Long append(String key,String value);
	
	/**
	 * 删除redis中保存的信息
	 * 
	 * @author pengcheng
	 * @since 2017年10月18日
	 * @param key redis中要删除的信息对应的token（键名）
	 * @return Long 执行结果状态码——1：表示执行成功
	 */
	public Long remove(String key);

	public Long hset(String key, String item, String value);

	public String hget(String key, String item);

	public Long incr(String key);

	public Long decr(String key);

	public Long expire(String key, int second);

	public Long ttl(String key);

	public Long hdel(String key, String item);
}
