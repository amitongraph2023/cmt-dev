package com.panera.cmt.dto.proxy.subscription_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GiftCoffeeSubscription {
    private Long customerId;
    private Long purchaseOrderId;
    private Long purchaseCafeId;
    private Date purchaseDateTime;
    private String purchaserEmail;
    private List<GiftCode> giftCodes;
}
