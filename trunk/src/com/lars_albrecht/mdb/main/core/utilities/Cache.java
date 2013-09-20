package com.lars_albrecht.mdb.main.core.utilities;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 */

/**
 * @author lalbrecht
 * 
 */
public class Cache {

	public static final int									CACHE_WEB	= 0;

	private static final ConcurrentHashMap<String, Object>	webCache	= new ConcurrentHashMap<String, Object>();

	public static void addToCache(final int cacheType, final String key, final Object value) {
		switch (cacheType) {
			case CACHE_WEB:
				Cache.webCache.put(key, value);
				break;
			default:
				try {
					throw new Exception("Unknown cacheType submitted: " + cacheType);
				} catch (final Exception e) {
					e.printStackTrace();
				}
		}
	}

	public static void clearCache(final int cacheType) {
		switch (cacheType) {
			case CACHE_WEB:
				Cache.webCache.clear();
				break;
			default:
				try {
					throw new Exception("Unknown cacheType submitted: " + cacheType);
				} catch (final Exception e) {
					e.printStackTrace();
				}
		}
	}

	public static Object getCacheEntry(final int cacheType, final String key) {
		switch (cacheType) {
			case CACHE_WEB:
				return Cache.webCache.get(key);
			default:
				try {
					throw new Exception("Unknown cacheType submitted: " + cacheType);
				} catch (final Exception e) {
					e.printStackTrace();
				}
		}
		return null;
	}

}
