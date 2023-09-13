package com.panera.cmt.dto.paytronix;

import lombok.Data;

@Data
public class TransactionDetailsDTO {
    private String balance;
    private String redeemed;
    private int walletCode;
    private String walletName;
    private String accrued;
}
