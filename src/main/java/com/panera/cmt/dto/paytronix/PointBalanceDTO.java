package com.panera.cmt.dto.paytronix;

import lombok.Data;

@Data
public class PointBalanceDTO {
    private String balance;
    private String description;
    private String name;
    private Long walletCode;
    private Boolean giftable;
}
