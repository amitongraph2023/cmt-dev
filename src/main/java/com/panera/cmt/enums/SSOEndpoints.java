package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SSOEndpoints {
    // Spoof Customer Session
    LOGIN_ADDRESS("/api/v1/login/impersonate/{customerId}"),
    LOGOUT_ADDRESS("/api/v1/token/{accessToken}");

    private String stub;
}
