package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.SubscriptionPrograms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionProgramsDTO {

  private Long activeProgramOptionId;
  private String autoApplyRewardCode;
  private String billingCountryDivision;
  private String billingPostalCode;
  private Boolean billingPostalCodeInvalid;
  private Boolean cafeDefaulted;
  private Long cafeId;
  private String cardBrand;
  private String clientType;
  private String customerFirstName;
  private Long customerId;
  private String destination;
  private String discountedUntilDate;
  private String enrollmentDate;
  private Boolean inProgress;
  private Long itemId;
  private String lastModifiedDate;
  private String lastPaymentProcessedDate;
  private String lastProgramOptionChangeMethod;
  private String lastRenewalReminderDate;
  private Long myReferralCount;
  private Long networkReferralCount;
  private String nextPaymentDate;
  private String paneraToken;
  private Long pendingProgramOptionId;
  private Long programId;
  private String programi18nName;
  private List<SubscriptionPromotionDTO> promotions;
  private String recurringTransactionId;
  private Boolean referralBonusApplied;
  private String referralBonusAppliedDate;
  private String referralCode;
  private String referredByCode;
  private Double renewalAmount;
  private String renewalDate;
  private Double renewalTax;
  private List<SubscriptionRenewalDTO> renewels;
  private String status;
  private Double subscriptionAmount;
  private String subscriptionId;
  private String subscriptionStatus;

  public static SubscriptionProgramsDTO fromEntity(SubscriptionPrograms entity) {
    return SubscriptionProgramsDTO.builder()
            .activeProgramOptionId(entity.getActiveProgramOptionId())
            .autoApplyRewardCode(entity.getAutoApplyRewardCode())
            .billingCountryDivision(entity.getBillingCountryDivision())
            .billingPostalCode(entity.getBillingPostalCode())
            .billingPostalCodeInvalid(entity.getBillingPostalCodeInvalid())
            .cafeDefaulted(entity.getCafeDefaulted())
            .cafeId(entity.getCafeId())
            .cardBrand(entity.getCardBrand())
            .clientType(entity.getClientType())
            .customerFirstName(entity.getCustomerFirstName())
            .customerId(entity.getCustomerId())
            .destination(entity.getDestination())
            .enrollmentDate(entity.getEnrollmentDate())
            .inProgress(entity.getInProgress())
            .itemId(entity.getItemId())
            .lastModifiedDate(entity.getLastModifiedDate())
            .lastPaymentProcessedDate(entity.getLastPaymentProcessedDate())
            .lastProgramOptionChangeMethod(entity.getLastProgramOptionChangeMethod())
            .lastRenewalReminderDate(entity.getLastRenewalReminderDate())
            .myReferralCount(entity.getMyReferralCount())
            .networkReferralCount(entity.getNetworkReferralCount())
            .nextPaymentDate(entity.getNextPaymentDate())
            .paneraToken(entity.getPaneraToken())
            .pendingProgramOptionId(entity.getPendingProgramOptionId())
            .programId(entity.getProgramId())
            .programi18nName(entity.getProgrami18nName())
            .promotions(entity.getPromotions())
            .recurringTransactionId(entity.getRecurringTransactionId())
            .referralBonusApplied(entity.getReferralBonusApplied())
            .referralBonusAppliedDate(entity.getReferralBonusAppliedDate())
            .referralCode(entity.getReferralCode())
            .referredByCode(entity.getReferredByCode())
            .renewalAmount(entity.getRenewalAmount())
            .renewalDate(entity.getRenewalDate())
            .renewalTax(entity.getRenewalTax())
            .renewels(entity.getRenewels())
            .status(entity.getStatus())
            .subscriptionAmount(entity.getSubscriptionAmount())
            .subscriptionId(entity.getSubscriptionId())
            .subscriptionStatus(entity.getSubscriptionStatus())
            .build();
  }

  public SubscriptionPrograms toEntity() {
    return SubscriptionPrograms.builder()
            .activeProgramOptionId(activeProgramOptionId)
            .autoApplyRewardCode(autoApplyRewardCode)
            .billingCountryDivision(billingCountryDivision)
            .billingPostalCode(billingPostalCode)
            .billingPostalCodeInvalid(billingPostalCodeInvalid)
            .cafeDefaulted(cafeDefaulted)
            .cafeId(cafeId)
            .cardBrand(cardBrand)
            .clientType(clientType)
            .customerFirstName(customerFirstName)
            .customerId(customerId)
            .destination(destination)
            .enrollmentDate(enrollmentDate)
            .inProgress(inProgress)
            .itemId(itemId)
            .lastModifiedDate(lastModifiedDate)
            .lastPaymentProcessedDate(lastPaymentProcessedDate)
            .lastProgramOptionChangeMethod(lastProgramOptionChangeMethod)
            .lastRenewalReminderDate(lastRenewalReminderDate)
            .myReferralCount(myReferralCount)
            .networkReferralCount(networkReferralCount)
            .nextPaymentDate(nextPaymentDate)
            .paneraToken(paneraToken)
            .pendingProgramOptionId(pendingProgramOptionId)
            .programId(programId)
            .programi18nName(programi18nName)
            .promotions(promotions)
            .recurringTransactionId(recurringTransactionId)
            .referralBonusApplied(referralBonusApplied)
            .referralBonusAppliedDate(referralBonusAppliedDate)
            .referralCode(referralCode)
            .referredByCode(referredByCode)
            .renewalAmount(renewalAmount)
            .renewalDate(renewalDate)
            .renewalTax(renewalTax)
            .renewels(renewels)
            .status(status)
            .subscriptionAmount(subscriptionAmount)
            .subscriptionId(subscriptionId)
            .subscriptionStatus(subscriptionStatus)
            .build();
  }
}
