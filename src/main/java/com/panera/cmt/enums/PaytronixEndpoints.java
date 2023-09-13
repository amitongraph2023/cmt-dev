package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaytronixEndpoints {
    BALANCE("/PaytronixProxy/card/{cardNumber}/balance")
    , TRANSACTION_HISTORY("/PaytronixProxy/card/{cardNumber}/transaction_history?dateStart={startDate}&onlyBAT=true")
    , WALLET_CODES("/PaytronixProxy/load_map")
    ;

    private String stub;
}
