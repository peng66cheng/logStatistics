//package com.daydays;
//
//import org.springframework.stereotype.Service;
//
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Element;
//
//@Service("myCache")
//public class MyCache {
//
//	private Cache cache;
//
//	// Create a cache manager
//	public MyCache() {
//		System.setProperty("net.sf.ehcache.enableShutdownHook", "true"); 
//		CacheManager cacheManager = CacheManager.create(MyCache.class.getResource("/cache/ehcache.xml"));
//		cache = cacheManager.getCache("ddsampleCache3");
//	}
//
//	public void putCache(String key, String value) {
//		final Element putGreeting = new Element(key, value);
//		cache.put(putGreeting);
//	}
//
//	public String getCache(String key) {
//		Element getGreeting = cache.get(key);
//		cache.flush();
//		return (String) getGreeting.getObjectValue();
//	}
//
//}
