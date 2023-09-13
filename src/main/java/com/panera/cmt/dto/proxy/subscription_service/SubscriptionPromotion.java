package com.panera.cmt.dto.proxy.subscription_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionPromotion {
    private Boolean affectsNextRenewal;
    private String autoRenewalDate;
    private Long displayPriority;
    private Boolean eligible;
    private String endDate;
    private String promoCode;
    private String promoDescription;
    private String promoType;
    private String scope;
    private String startDate;
    private String status;
    private Double subscriptionAmount;
    private Long walletCode;
}
