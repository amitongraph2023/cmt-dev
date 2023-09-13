package com.panera.cmt.dto.paytronix;

import lombok.Data;

@Data
public class WalletsDTO {
    private String responseMessage;
    private WalletDTO[] wallets;
}
