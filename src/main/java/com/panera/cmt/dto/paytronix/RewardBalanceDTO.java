package com.panera.cmt.dto.paytronix;

import lombok.Data;

@Data
public class RewardBalanceDTO {
    private String balance;
    private String name;
    private ExpirationDTO[] expirations;
    private Long walletCode;
    private Boolean giftable;
}
