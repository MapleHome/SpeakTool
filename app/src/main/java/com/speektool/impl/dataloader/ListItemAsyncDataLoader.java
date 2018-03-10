package com.speektool.impl.dataloader;

import android.support.v4.util.LruCache;

import com.speektool.api.AsyncDataLoader;
import com.speektool.tasks.ThreadPoolWrapper;

public abstract class ListItemAsyncDataLoader<K, V> implements AsyncDataLoader<K, V> {

	private static final String tag = ListItemAsyncDataLoader.class.getSimpleName();

	private static final int HALF_PROCESSORS = Runtime.getRuntime().availableProcessors() / 2;

	public static final int THREAD_AMOUNT = HALF_PROCESSORS < 1 ? 1 : HALF_PROCESSORS;

	private ThreadPoolWrapper pool = ThreadPoolWrapper.newThreadPool(THREAD_AMOUNT);

	public ListItemAsyncDataLoader() {
		super();
	}

	private LruCache<K, V> cachedValues = new LruCache<K, V>(getCacheSize()) {
		@Override
		protected int sizeOf(K key, V value) {

			return getSizeOfValueBytes(key, value);
		}
	};

	protected int getCacheSize() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 16;
		return cacheSize;
	}

	protected abstract int getSizeOfValueBytes(K key, V value);

	@Override
	public V load(K key, Object... args) {
		V cache = getCache(key);
		if (isValueValid(cache)) {
			return cache;
		}
		cachedValues.remove(key);
		pool.execute(new Task(key, args));
		return null;

	}

	protected abstract V getDataLogic(K key, Object... args);

	protected boolean isValueValid(V value) {
		return value != null;
	}

	public class Task implements Runnable {
		private K key;
		private Object[] args;

		public Task(K key, Object... args) {
			super();
			this.key = key;
			this.args = args;

		}

		@Override
		public void run() {
			V v = getDataLogic(key, args);
			if (v != null) {
				cachedValues.put(key, v);
				postFinishEvent(key, v, args);
			} else {
				postErrorEvent(key, v, args);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;
			if (o.getClass() != this.getClass())
				return false;
			Task input = (Task) o;
			return this.key.equals(input.key);
		}

	}

	protected abstract void postFinishEvent(K key, V value, Object... args);

	protected abstract void postErrorEvent(K key, V value, Object... args);

	@Override
	public void destroy() {
		pool.shutdownNow();
		cachedValues.evictAll();
	}

	@Override
	protected void finalize() throws Throwable {
		pool.shutdownNow();
		super.finalize();
	}

	@Override
	public void cancelAll() {
		pool.cancelAllWaitingTask();
	}

	@Override
	public V getCache(K key) {
		V cache = cachedValues.get(key);
		return cache;
	}

}
