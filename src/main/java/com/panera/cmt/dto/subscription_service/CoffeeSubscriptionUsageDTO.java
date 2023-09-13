package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.CoffeeSubscriptionUsage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CoffeeSubscriptionUsageDTO {
    private String transactionType;
    private String status;
    private String subscriptionAmount;
    private String tax;
    private String totalAmount;
    private String cardType;
    private String lastFour;
    private String billingPostalCode;
    private String promoCode;
    private Date   renewalDate;
    private Date   rewardExpirationDate;

    private String inProgress;
    private String subscriptionId;
    private String customerId;
    private String programId;
    private String itemId;
    private Date lastModifiedDate;
    private String cafeId;
    private String paytypeId;
    private String orderId;
    private String walletCode;
    private String paymentTransId;
    private String clientType;
    private String coreStatus;
    private String tokenValue;
    private String tokenType;
    private String authCode;

    public static CoffeeSubscriptionUsageDTO fromEntity(CoffeeSubscriptionUsage entity) {
        return CoffeeSubscriptionUsageDTO.builder()
                .transactionType(entity.getTransactionType())
                .status(entity.getStatus())
                .orderId(entity.getOrderId())
                .subscriptionAmount(entity.getSubscriptionAmount())
                .tax(entity.getTax())
                .totalAmount(entity.getTotalAmount())
                .cardType(entity.getCardType())
                .lastFour(entity.getLastFour())
                .cafeId(entity.getCafeId())
                .billingPostalCode(entity.getBillingPostalCode())
                .promoCode(entity.getPromoCode())
                .renewalDate(entity.getRenewalDate())
                .rewardExpirationDate(entity.getRewardExpirationDate())
                .inProgress(entity.getInProgress())
                .subscriptionId(entity.getSubscriptionId())
                .customerId(entity.getCustomerId())
                .programId(entity.getProgramId())
                .itemId(entity.getItemId())
                .lastModifiedDate(entity.getLastModifiedDate())
                .cafeId(entity.getCafeId())
                .paytypeId(entity.getPaytypeId())
                .orderId(entity.getOrderId())
                .walletCode(entity.getWalletCode())
                .paymentTransId(entity.getPaymentTransId())
                .clientType(entity.getClientType())
                .coreStatus(entity.getCoreStatus())
                .tokenType(entity.getTokenType())
                .tokenValue(entity.getTokenValue())
                .authCode(entity.getAuthCode())
                .build();
    }

    public CoffeeSubscriptionUsage toEntity() {
        return CoffeeSubscriptionUsage.builder()
                .transactionType(getTransactionType())
                .status(getStatus())
                .orderId(getOrderId())
                .subscriptionAmount(getSubscriptionAmount())
                .tax(getTax())
                .totalAmount(getTotalAmount())
                .cardType(getCardType())
                .lastFour(getLastFour())
                .cafeId(getCafeId())
                .billingPostalCode(getBillingPostalCode())
                .promoCode(getPromoCode())
                .renewalDate(getRenewalDate())
                .rewardExpirationDate(getRewardExpirationDate())
                .inProgress(getInProgress())
                .subscriptionId(getSubscriptionId())
                .customerId(getCustomerId())
                .programId(getProgramId())
                .itemId(getItemId())
                .lastModifiedDate(getLastModifiedDate())
                .cafeId(getCafeId())
                .paytypeId(getPaytypeId())
                .orderId(getOrderId())
                .walletCode(getWalletCode())
                .paymentTransId(getPaymentTransId())
                .clientType(getClientType())
                .coreStatus(getCoreStatus())
                .tokenType(getTokenType())
                .tokenValue(getTokenValue())
                .authCode(getAuthCode())
                .build();
    }
}

