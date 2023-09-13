package com.panera.cmt.dto.proxy.subscription_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GiftCode {
    private String code;
    private String status;
    private Date redeemedDateTime;
    private String claimDateTime;
    private String description;
    private String giftEndDate;
    private String program;
    private Long purchaseItemId;
    private Long redemptionCafeId;
    private Long redemptionCustomerId;
    private Long redemptionItemId;
    private Long redemptionOrderId;
    private Long walletCodeApplied;
}
