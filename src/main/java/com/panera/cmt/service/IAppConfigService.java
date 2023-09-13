package com.panera.cmt.service;

import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface IAppConfigService {
    Optional<ResponseHolder<AppConfig>> createAppConfig(AppConfig appConfig);

    Optional<ResponseHolder<Boolean>> deleteAppConfigById(Long id);

    Optional<ResponseHolder<Page<AppConfig>>> searchAppConfigPaged(String query, Integer pageNumber, Integer pageSize, Sort.Direction dir, AppConfigSortColumn col);

    Optional<ResponseHolder<AppConfig>> updateAppConfig(Long id, AppConfig updatedEntity);
}
