package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UpdateAccountStatusAction {
    REINSTATE("reinstateAccount"),
    SUSPEND("suspendAccount"),
    TERMINATE("terminateAccount"),
    PROTECT("protect"),
    PENDING("pending");

    private String routeParamName;
}
