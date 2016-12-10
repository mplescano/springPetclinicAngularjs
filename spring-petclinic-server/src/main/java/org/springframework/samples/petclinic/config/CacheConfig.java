package org.springframework.samples.petclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.CacheManager;

/**
 * Cache could be disable in unit test.
 */
@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	@Autowired
	public EhCacheCacheManager ehCacheCacheManager(CacheManager cacheManager) {
		EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
		ehCacheCacheManager.setCacheManager(cacheManager);
		return ehCacheCacheManager;
	}

	@Bean
	public EhCacheManagerFactoryBean cacheManager() {
		EhCacheManagerFactoryBean ehCacheManager = new EhCacheManagerFactoryBean();
		//ehCacheManager.setConfigLocation(new ClassPathResource("cache/ehcache.xml"));
		return ehCacheManager;
	}

}
