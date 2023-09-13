package com.panera.cmt.dto.paytronix;

import lombok.Data;

@Data
public class WalletDTO {
    private Long scale;
    private String posItemId;
    private String valueAmount;
    private Long walletType;
    private Long discountObjectNumber;
    private Long rewardType;
    private Long productType;
    private Long productId;
    private String walletName;
    private Long walletContents;
    private Long valueType;
    private Long walletCode;
}
