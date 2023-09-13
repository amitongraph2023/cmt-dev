package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GiftCoffeeEmailDTO {
    private Long customerId;
    private Long purchaseOrderId;
    private String code;
    private String purchaserEmail;
    private String description;
    private String program;

    public static GiftCoffeeEmailDTO fromEntity(GiftCoffeeEmail entity) {
        return GiftCoffeeEmailDTO.builder()
                .customerId(entity.getCustomerId())
                .purchaseOrderId(entity.getPurchaseOrderId())
                .code(entity.getCode())
                .purchaserEmail(entity.getPurchaserEmail())
                .description(entity.getDescription())
                .program(entity.getProgram())
                .build();
    }

    public GiftCoffeeEmail toEntity() {
        return GiftCoffeeEmail.builder()
                .customerId(customerId)
                .purchaseOrderId(purchaseOrderId)
                .code(code)
                .purchaserEmail(purchaserEmail)
                .description(description)
                .program(program)
                .build();
    }
}
