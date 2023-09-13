package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EpsEndpoints {
    // App Config
    APP_CONFIG_BASE("/api/v1/app-config"),
    APP_CONFIG_BY_ID("/api/v1/app-config/{id}"),
    APP_CONFIG_PAGE("/api/v1/app-config?col={col}&dir={dir}&page={pageNumber}&query={query}&size={pageSize}");

    private String stub;
}
