package org.springframework.samples.petclinic.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.ConfigurationFactory;

/**
 * Cache could be disable in unit test.
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

	@Bean
	public CacheManager cacheManager() {
		net.sf.ehcache.config.Configuration configuration = ConfigurationFactory.parseConfiguration();
		net.sf.ehcache.CacheManager ehcacheManager = new net.sf.ehcache.CacheManager(configuration);
		
		CacheConfiguration cacheConf = new CacheConfiguration("vets", 100)
				.diskExpiryThreadIntervalSeconds(60);
		ehcacheManager.addCache(new Cache(cacheConf));

		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehcacheManager);
		
		return cacheManager;
	}

}
