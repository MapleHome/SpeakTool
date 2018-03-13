package com.speaktool.api;

/**
 * 异步数据加载
 * 
 * @author shaoshuai
 * 
 * @param <K>
 * @param <V>
 */
public interface AsyncDataLoader<K, V> {
	/**
	 * 加载
	 */
	V load(K key, Object... args);

	/**
	 * 销毁所有加载线程
	 */
	void destroy();

	/**
	 * 取消所有
	 */
	void cancelAll();

	/**
	 * 获取缓存
	 */
	V getCache(K key);

}
