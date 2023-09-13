package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpoofUnitType {
    IWEB("Retail Spoof"),
    CATERING("Catering3 Spoof"),
    RETAIL("Retail Spoof");

    private String xOriginSource;
}
