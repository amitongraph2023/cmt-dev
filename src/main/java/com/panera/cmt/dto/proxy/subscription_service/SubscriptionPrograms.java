package com.panera.cmt.dto.proxy.subscription_service;

import com.panera.cmt.dto.subscription_service.SubscriptionPromotionDTO;
import com.panera.cmt.dto.subscription_service.SubscriptionRenewalDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionPrograms {


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

}

