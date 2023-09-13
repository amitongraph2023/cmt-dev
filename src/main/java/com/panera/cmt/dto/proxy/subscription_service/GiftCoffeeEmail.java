package com.panera.cmt.dto.proxy.subscription_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GiftCoffeeEmail {
    private Long customerId;
    private Long purchaseOrderId;
    private String code;
    private String purchaserEmail;
    private String description;
    private String program;
}
