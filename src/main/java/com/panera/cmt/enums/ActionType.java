package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActionType {
    ADD_REWARD("Add Reward")
    , CREATE("Create")
    , DELETE("Delete")
    , REACTIVATE("Reactivate Account")
    , REDEEM("Redeem")
    , RESEND("Resend")
    , REINSTATE("Reinstate Account")
    , SPOOF("Spoof")
    , SUSPEND("Suspend Account")
    , UPDATE("Update")
    , VALIDATE("Validate")
    ;

    private String displayName;
}
