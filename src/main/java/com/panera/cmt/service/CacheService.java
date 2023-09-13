package com.panera.cmt.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import static com.panera.cmt.config.cache.CacheConfig.*;

@Service
public class CacheService implements ICacheService {

    @Caching(evict = {
            @CacheEvict(value = {CACHE_APP_CONFIG, CACHE_APP_CONFIG_AUTH_GROUPS, CACHE_APP_CONFIG_ALL_AUTH_GROUPS,
                    CACHE_APP_CONFIG_SEARCH, CACHE_APP_UI_GENERIC, CACHE_APP_UI_ROUTE_WHITELISTS, CACHE_APP_UI_PERMISSION_WHITELIST})
    })
    @Override
    public boolean clearAllCache() {
        return true;
    }

}
