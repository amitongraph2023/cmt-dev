package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppConfigDomain {
    CMT("CMT")
    , CHUB("CustHub")
    , EPS("EPS");

    private String displayName;
}


