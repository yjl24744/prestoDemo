package com.example.demo.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class IaeCacheManager {
	private static CacheManager cacheManager;

	IaeCacheManager(CacheManager cacheManager) {
		IaeCacheManager.cacheManager = cacheManager;
	}

	public static Cache getDefaultCache() {
		return getCache(CacheKey.EXPIRE_24_HOURS);
	}

	public static Cache getCache(CacheKey key) {
		return getCache(key.toString());
	}

	protected static Cache getCache(String name) {
		if (cacheManager == null) {
			return null;
		}
		return cacheManager.getCache(name);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}	
}
