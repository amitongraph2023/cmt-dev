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
public class CoffeeSubscriptionUsage {

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
}
