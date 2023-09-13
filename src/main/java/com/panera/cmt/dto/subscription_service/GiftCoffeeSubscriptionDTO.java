package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.GiftCode;
import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeSubscription;
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
public class GiftCoffeeSubscriptionDTO {
    private Long customerId;
    private Long purchaseOrderId;
    private Long purchaseCafeId;
    private Date purchaseDateTime;
    private String purchaserEmail;
    private List<GiftCode> giftCodes;

        public static GiftCoffeeSubscriptionDTO fromEntity(GiftCoffeeSubscription entity) {
        return GiftCoffeeSubscriptionDTO.builder()
                .customerId(entity.getCustomerId())
                .purchaseOrderId(entity.getPurchaseOrderId())
                .purchaseCafeId(entity.getPurchaseCafeId())
                .purchaseDateTime(entity.getPurchaseDateTime())
                .purchaserEmail(entity.getPurchaserEmail())
                .giftCodes(entity.getGiftCodes())
                .build();
    }

    public GiftCoffeeSubscription toEntity() {
        return GiftCoffeeSubscription.builder()
                .customerId(customerId)
                .purchaseOrderId(purchaseOrderId)
                .purchaseCafeId(purchaseCafeId)
                .purchaseDateTime(purchaseDateTime)
                .purchaserEmail(purchaserEmail)
                .giftCodes(giftCodes)
                .build();
    }
}
