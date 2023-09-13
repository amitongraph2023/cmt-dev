package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LMTEndpoints {
    TRANSACTION_HISTORY("/specialCodeDiscount/{specialCode}");

    private String stub;
}