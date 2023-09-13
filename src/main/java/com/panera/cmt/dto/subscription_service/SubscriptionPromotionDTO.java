package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.SubscriptionPromotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionPromotionDTO {
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

      public static SubscriptionPromotionDTO fromEntity(SubscriptionPromotion entity) {
            return SubscriptionPromotionDTO.builder()
                    .affectsNextRenewal(entity.getAffectsNextRenewal())
                    .autoRenewalDate(entity.getAutoRenewalDate())
                    .displayPriority(entity.getDisplayPriority())
                    .eligible(entity.getEligible())
                    .endDate(entity.getEndDate())
                    .promoCode(entity.getPromoCode())
                    .promoDescription(entity.getPromoDescription())
                    .promoType(entity.getPromoType())
                    .scope(entity.getScope())
                    .startDate(entity.getStartDate())
                    .status(entity.getStatus())
                    .subscriptionAmount(entity.getSubscriptionAmount())
                    .walletCode(entity.getWalletCode())
                    .build();
      }

      public SubscriptionPromotion toEntity() {
            return SubscriptionPromotion.builder()
                    .affectsNextRenewal(affectsNextRenewal)
                    .autoRenewalDate(autoRenewalDate)
                    .displayPriority(displayPriority)
                    .eligible(eligible)
                    .endDate(endDate)
                    .promoCode(promoCode)
                    .promoDescription(promoDescription)
                    .promoType(promoType)
                    .scope(scope)
                    .startDate(startDate)
                    .status(status)
                    .subscriptionAmount(subscriptionAmount)
                    .walletCode(walletCode)
                    .build();
      }
}
