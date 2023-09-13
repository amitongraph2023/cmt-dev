package com.panera.cmt.dto.proxy.subscription_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionRenewal {
    private Double discountAmount;
    private String renewalDate;
    private Double subtotal;
    private Double taxAmount;
    private Double total;
}
