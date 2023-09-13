package com.panera.cmt.service.app_config;

import com.panera.cmt.entity.AppConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.panera.cmt.config.Constants.*;

@Service
public class AuthGroupsService extends AppConfigService implements IAuthGroupsService {

    @Cacheable(CACHE_APP_CONFIG_AUTH_GROUPS)
    @Override
    public Map<String, List<String>> getAuthGroups() {
        return getValuesArrayMap(APP_CONFIG_AUTH_GROUP);
    }

    @Cacheable(CACHE_APP_CONFIG_ALL_AUTH_GROUPS)
    @Override
    public List<String> getAllAuthGroups() {
        return searchAppConfigByCode(APP_CONFIG_AUTH_GROUP)
                .map(results -> {
                    List<String> authGroups = new ArrayList<>();

                    for (AppConfig result : results) {
                        if (result != null) {
                            authGroups.addAll(result.getStringList(","));
                        }
                    }

                    return authGroups;
                })
                .orElse(new ArrayList<>());
    }
}
