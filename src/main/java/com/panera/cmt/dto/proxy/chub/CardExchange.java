package com.panera.cmt.dto.proxy.chub;

import lombok.Data;

@Data
public class CardExchange {
    private String cardNumber;
    private boolean opt;
    private String regCode;
}
