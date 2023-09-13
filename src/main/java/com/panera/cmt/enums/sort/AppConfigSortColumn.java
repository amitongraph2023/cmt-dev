package com.panera.cmt.enums.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppConfigSortColumn {
    CODE("code"),
    VALUE("value");

    private String name;
}
