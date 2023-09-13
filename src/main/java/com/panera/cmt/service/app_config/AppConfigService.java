package com.panera.cmt.service.app_config;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.AppConfigDomain;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import com.panera.cmt.repository.IAppConfigRepository;
import com.panera.cmt.service.chub.ChubAppConfigService;
import com.panera.cmt.service.eps.EpsAppConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.panera.cmt.config.Constants.*;

@Service
@Slf4j
public class AppConfigService implements IAppConfigLocalService {

    private IAppConfigRepository appConfigRepository;
    private ChubAppConfigService chubAppConfigService;
    private EpsAppConfigService epsAppConfigService;

    @Autowired
    public void setAppConfigRepository(IAppConfigRepository appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    @Autowired
    public void setChubAppConfigService(ChubAppConfigService chubAppConfigService) {
        this.chubAppConfigService = chubAppConfigService;
    }

    @Autowired
    public void setEpsAppConfigService(EpsAppConfigService epsAppConfigService) {
        this.epsAppConfigService = epsAppConfigService;
    }

    @CacheEvict(value = {CACHE_APP_CONFIG, CACHE_APP_UI_GENERIC, CACHE_APP_CONFIG_SEARCH, CACHE_APP_UI_ROUTE_WHITELISTS})
    @Override
    public Optional<ResponseHolder<AppConfig>> createLocalAppConfig(AppConfig appConfig) {
        appConfig.setId(null);
        return Optional.of(new ResponseHolder<AppConfig>(appConfigRepository.save(appConfig)));
    }

    @CacheEvict(value = {CACHE_APP_CONFIG, CACHE_APP_UI_GENERIC, CACHE_APP_CONFIG_SEARCH, CACHE_APP_UI_ROUTE_WHITELISTS})
    @Override
    public Optional<ResponseHolder<AppConfig>> createRemoteAppConfig(AppConfig appConfig, AppConfigDomain domain) {
        switch(domain) {
            case CHUB:
                return chubAppConfigService.createAppConfig(appConfig);
            case EPS:
                return epsAppConfigService.createAppConfig(appConfig);
            default:
                return Optional.empty();
        }
    }

    @Override
    public boolean doesAppConfigExist(Long id) {
        return appConfigRepository.existsById(id);
    }

    @Override
    public boolean doesAppConfigExist(String code) {
        return appConfigRepository.existsByCode(code);
    }

    @Override
    public boolean doesAppConfigExistExcludeId(String code, Long id) {
        return appConfigRepository.getByCode(code).map(appConfig -> !appConfig.getId().equals(id)).orElse(false);
    }

    @Override
    public Optional<ResponseHolder<Boolean>> deleteLocalAppConfigById(Long id) {
        appConfigRepository.deleteById(id);
        return Optional.of(new ResponseHolder<>(true, new AllErrorsDTO(), HttpStatus.NO_CONTENT));
    }

    @Override
    public Optional<ResponseHolder<Boolean>> deleteRemoteAppConfigById(Long id, AppConfigDomain domain) {
        switch (domain) {
            case CHUB:
                return chubAppConfigService.deleteAppConfigById(id);
            case EPS:
                return epsAppConfigService.deleteAppConfigById(id);
            default:
                return Optional.empty();
        }
    }

    @Cacheable(CACHE_APP_CONFIG)
    @Override
    public Optional<AppConfig> getAppConfigByCode(String code) {
        return appConfigRepository.getByCode(code);
    }

    @Override
    public Optional<String> getAppConfigValueByCode(String code) {
        return getAppConfigByCode(code).map(AppConfig::getValue);
    }

    @Override
    public Optional<Integer> getAppConfigIntValueByCode(String code) {
        return getAppConfigByCode(code).map(appConfig -> Integer.valueOf(appConfig.getValue()));
    }

    @Override
    public Optional<TimeUnit> getAppConfigTimeEnumValueByCode(String code) {
        return getAppConfigByCode(code).map(appConfig -> {
            String value = appConfig.getValue();
            TimeUnit timeUnit = null;
            if (value.equalsIgnoreCase("SECONDS")) {
                timeUnit = TimeUnit.SECONDS;
            }
            if (value.equalsIgnoreCase("MINUTES")) {
                timeUnit = TimeUnit.MINUTES;
            }
            if (value.equalsIgnoreCase("HOURS")) {
                timeUnit = TimeUnit.HOURS;
            }
            if (value.equalsIgnoreCase("DAYS")) {
                timeUnit = TimeUnit.DAYS;
            }
            return timeUnit;
        });
    }

    @Cacheable(CACHE_APP_CONFIG_SEARCH)
    @Override
    public Optional<List<AppConfig>> searchAppConfigByCode(String code) {
        return Optional.ofNullable(appConfigRepository.searchByCode(code));
    }

    @Override
    public Optional<ResponseHolder<Page<AppConfig>>> searchLocalAppConfigPaged(String query, Integer pageNumber, Integer pageSize, Sort.Direction dir, AppConfigSortColumn col) {
        if (dir == null) {
            dir = SORT_DIR_APP_CONFIG;
        }
        if (col == null) {
            col = SORT_COL_APP_CONFIG;
        }
        return Optional.of(new ResponseHolder<>(
                appConfigRepository.searchAppConfigPaged((query != null) ? query : "", PageRequest.of(pageNumber - 1, pageSize, new Sort(dir, col.getName())))));
    }

    @Override
    public Optional<ResponseHolder<Page<AppConfig>>> searchRemoteAppConfigPaged(String query, Integer pageNumber, Integer pageSize, Sort.Direction dir, AppConfigSortColumn col, AppConfigDomain domain) {
        switch (domain) {
           case CHUB:
                return chubAppConfigService.searchAppConfigPaged(query, pageNumber, pageSize, dir, col);
            case EPS:
                return epsAppConfigService.searchAppConfigPaged(query, pageNumber, pageSize, dir, col);
            default:
                return Optional.empty();
        }
    }

    @CacheEvict(value = {CACHE_APP_CONFIG, CACHE_APP_CONFIG_SEARCH, CACHE_APP_UI_ROUTE_WHITELISTS})
    @Override
    public Optional<ResponseHolder<AppConfig>> updateLocalAppConfig(Long id, AppConfig updatedEntity) {
        return appConfigRepository.findById(id)
                .map(existingAppConfig -> {
                    existingAppConfig.setCode(updatedEntity.getCode());
                    existingAppConfig.setValue(updatedEntity.getValue());

                    return new ResponseHolder<>(appConfigRepository.save(existingAppConfig));
                });
    }

    @Override
    public Optional<ResponseHolder<AppConfig>> updateRemoteAppConfig(Long id, AppConfig updatedEntity, AppConfigDomain domain) {
        switch (domain) {
            case CHUB:
                return chubAppConfigService.updateAppConfig(id, updatedEntity);
            case EPS:
                return epsAppConfigService.updateAppConfig(id, updatedEntity);
            default:
                return Optional.empty();
        }
    }

    protected Map<String, List<String>> getValuesArrayMap(String code) {
        return searchAppConfigByCode(code)
                .map(results -> {
                    Map<String, List<String>> authGroups = new HashMap<>();

                    for (AppConfig result : results) {
                        if (result != null) {
                            authGroups.put(result.getCode(), result.getStringList(","));
                        }
                    }

                    return authGroups;
                })
                .orElse(new HashMap<>());
    }
    
}
