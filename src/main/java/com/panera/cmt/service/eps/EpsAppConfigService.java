package com.panera.cmt.service.eps;

import com.google.gson.Gson;
import com.panera.cmt.dto.app_config.AppConfigDTO;
import com.panera.cmt.dto.app_config.PageOfAppConfigIncoming;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.AppConfigDomain;
import com.panera.cmt.enums.EpsEndpoints;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import com.panera.cmt.service.IAppConfigService;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panera.cmt.config.Constants.SORT_COL_APP_CONFIG;
import static com.panera.cmt.config.Constants.SORT_DIR_APP_CONFIG;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class EpsAppConfigService extends BaseEnterpriseProspectService implements IAppConfigService {

    private AppConfigDomain domain = AppConfigDomain.EPS;

    @Override
    public Optional<ResponseHolder<AppConfig>> createAppConfig(AppConfig appConfig) {
        StopWatch stopWatch = new StopWatch(log, "createAppConfig", String.format("Creating appConfig entry on domain=%s with code=%s", domain.getDisplayName(), appConfig.getCode()));
        return Optional.ofNullable(doPost(AppConfig.class, stopWatch, null, AppConfigDTO.fromEntity(appConfig), EpsEndpoints.APP_CONFIG_BASE));
    }

    @Override
    public Optional<ResponseHolder<Boolean>> deleteAppConfigById(Long id) {
        StopWatch stopWatch = new StopWatch(log, "deleteAppConfig", String.format("Deleting appConfig entry on domain=%s with id=%d", domain.getDisplayName(), id));
        ResponseHolder<Boolean> response = doDelete(Boolean.class, stopWatch, null, EpsEndpoints.APP_CONFIG_BY_ID, id);

        switch(response.getStatus()) {
            case NO_CONTENT:
                response.setEntity(true);
                response.setStatus(HttpStatus.NO_CONTENT);
                response.setErrors(response.getErrors());
                break;
            case NOT_FOUND:
                response.setEntity(false);
                response.setStatus(HttpStatus.NOT_FOUND);
                response.setErrors(response.getErrors());
                break;
            default:
                response.setEntity(null);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setErrors(response.getErrors());
        }

        return Optional.of(response);
    }

    @Override
    public Optional<ResponseHolder<Page<AppConfig>>> searchAppConfigPaged(String query, Integer pageNumber, Integer pageSize, Sort.Direction dir, AppConfigSortColumn col) {
        StopWatch stopWatch = new StopWatch(log, "searchAppConfigPaged", String.format("Searching appConfig on domain=%s and getting results paged", domain.getDisplayName()));
        ResponseHolder<String> response =  doGetResponse(String.class, stopWatch, EpsEndpoints.APP_CONFIG_PAGE, col, dir, pageNumber, query, pageSize);

        ResponseHolder<Page<AppConfig>> returnValue;

        switch (response.getStatus()) {
            case OK:
                Page<AppConfig> appConfigPage = Page.empty();
                List<AppConfig> appConfigs = new ArrayList<>();

                if (response.getEntity() != null) {
                    PageOfAppConfigIncoming page = new Gson().fromJson(response.getEntity(), PageOfAppConfigIncoming.class);
                    for (AppConfig appConfig : page.getContent()){
                        appConfigs.add(appConfig);
                    }

                    if (page.getSortDir() == null) {
                        page.setSortDir(SORT_DIR_APP_CONFIG);
                    }
                    if (col == null) {
                        col = SORT_COL_APP_CONFIG;
                    }

                    Pageable pageable = PageRequest.of(
                            page.getNumber()
                            , page.getSize()
                            , page.getSortDir().equals(Sort.Direction.ASC) ? Sort.by(col.getName()).ascending() : Sort.by(col.getName()).descending()
                    );

                    appConfigPage = new PageImpl<>(appConfigs, pageable, appConfigs.size());
                }

                returnValue = new ResponseHolder<>(appConfigPage, response.getErrors(), response.getStatus());
                break;
            default:
                returnValue = new ResponseHolder<>(null, response.getErrors(), response.getStatus());
        }

        return Optional.of(returnValue);
    }

    @Override
    public Optional<ResponseHolder<AppConfig>> updateAppConfig(Long id, AppConfig updatedEntity) {
        if (isNull(id, updatedEntity)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateAppConfig", String.format("Updating appConfig entry on domain=%s with id=%d", domain, id));

        return Optional.ofNullable(doPut(AppConfig.class, stopWatch, null, updatedEntity, EpsEndpoints.APP_CONFIG_BY_ID, id));
    }

    @Override
    protected String getSubjectName() {
        return null;
    }
}

