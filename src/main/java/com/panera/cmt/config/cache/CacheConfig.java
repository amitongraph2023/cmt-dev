package com.panera.cmt.config.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    public static final String CACHE_APP_CONFIG = "AppConfig";
    public static final String CACHE_APP_CONFIG_AUTH_GROUPS = "AppConfigAuthGroups";
    public static final String CACHE_APP_CONFIG_ALL_AUTH_GROUPS = "AppConfigAllAuthGroups";
    public static final String CACHE_APP_CONFIG_SEARCH = "AppConfigSearch";
    public static final String CACHE_APP_UI_GENERIC = "AppConfigUIGeneric";
    public static final String CACHE_APP_UI_ROUTE_WHITELISTS = "AppConfigUIRoutesWhiteList";
    public static final String CACHE_APP_UI_PERMISSION_WHITELIST = "AppConfigUIPermissionWhiteList";

    @Override
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {

            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name,
                        CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(1000).build().asMap(), false);
            }
        };
        return cacheManager;
    }
}
