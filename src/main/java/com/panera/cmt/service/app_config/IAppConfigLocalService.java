package com.panera.cmt.service.app_config;

import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.AppConfigDomain;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface IAppConfigLocalService {

    Optional<ResponseHolder<AppConfig>> createLocalAppConfig(AppConfig appConfig);
    Optional<ResponseHolder<AppConfig>> createRemoteAppConfig(AppConfig appConfig, AppConfigDomain domain);

    boolean doesAppConfigExist(Long id);

    boolean doesAppConfigExist(String code);

    boolean doesAppConfigExistExcludeId(String code, Long id);

    Optional<ResponseHolder<Boolean>> deleteLocalAppConfigById(Long id);
    Optional<ResponseHolder<Boolean>> deleteRemoteAppConfigById(Long id, AppConfigDomain domain);

    Optional<AppConfig> getAppConfigByCode(String code);

    Optional<Integer> getAppConfigIntValueByCode(String code);

    Optional<TimeUnit> getAppConfigTimeEnumValueByCode(String code);

    Optional<String> getAppConfigValueByCode(String code);

    Optional<List<AppConfig>> searchAppConfigByCode(String code);

    Optional<ResponseHolder<Page<AppConfig>>> searchLocalAppConfigPaged(String query, Integer pageNumber, Integer pageSize, Sort.Direction dir, AppConfigSortColumn col);
    Optional<ResponseHolder<Page<AppConfig>>> searchRemoteAppConfigPaged(String query, Integer pageNumber, Integer pageSize, Sort.Direction dir, AppConfigSortColumn col, AppConfigDomain domain);

    Optional<ResponseHolder<AppConfig>> updateLocalAppConfig(Long id, AppConfig updatedEntity);
    Optional<ResponseHolder<AppConfig>> updateRemoteAppConfig(Long id, AppConfig updatedEntity, AppConfigDomain domain);
}
