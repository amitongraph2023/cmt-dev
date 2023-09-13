package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.SubscriptionRenewal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionRenewalDTO {
      private Double discountAmount;
      private String renewalDate;
      private Double subtotal;
      private Double taxAmount;
      private Double total;

      public static SubscriptionRenewalDTO fromEntity(SubscriptionRenewal entity) {
            return SubscriptionRenewalDTO.builder()
                    .discountAmount(entity.getDiscountAmount())
                    .renewalDate(entity.getRenewalDate())
                    .subtotal(entity.getSubtotal())
                    .taxAmount(entity.getTaxAmount())
                    .total(entity.getTotal())
                    .build();
      }

      public SubscriptionRenewal toEntity() {
            return SubscriptionRenewal.builder()
                    .discountAmount(discountAmount)
                    .renewalDate(renewalDate)
                    .subtotal(subtotal)
                    .taxAmount(taxAmount)
                    .build();
      }
}
