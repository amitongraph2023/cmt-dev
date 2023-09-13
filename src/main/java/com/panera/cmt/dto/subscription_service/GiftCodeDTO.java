package com.panera.cmt.dto.subscription_service;

import com.panera.cmt.dto.proxy.subscription_service.GiftCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GiftCodeDTO {
    private String code;
    private String status;
    private Date redeemedDateTime;
    private String claimDateTime;
    private String description;
    private String giftEndDate;
    private String program;
    private Long purchaseItemId;
    private Long redemptionCafeId;
    private Long redemptionCustomerId;
    private Long redemptionItemId;
    private Long redemptionOrderId;
    private Long walletCodeApplied;

    public static GiftCodeDTO fromEntity(GiftCode entity) {
        return GiftCodeDTO.builder()
                .code(entity.getCode())
                .status(entity.getStatus())
                .redeemedDateTime(entity.getRedeemedDateTime())
                .claimDateTime(entity.getClaimDateTime())
                .description(entity.getDescription())
                .giftEndDate(entity.getGiftEndDate())
                .program(entity.getProgram())
                .purchaseItemId(entity.getPurchaseItemId())
                .redemptionCafeId(entity.getRedemptionCafeId())
                .redemptionCustomerId(entity.getRedemptionCustomerId())
                .redemptionItemId(entity.getRedemptionItemId())
                .redemptionOrderId(entity.getRedemptionOrderId())
                .walletCodeApplied(entity.getWalletCodeApplied())
                .build();
    }

    public GiftCode toEntity() {
        return GiftCode.builder()
                .code(code)
                .status(status)
                .redeemedDateTime(redeemedDateTime)
                .claimDateTime(claimDateTime)
                .description(description)
                .giftEndDate(giftEndDate)
                .program(program)
                .purchaseItemId(purchaseItemId)
                .redemptionCafeId(redemptionCafeId)
                .redemptionCustomerId(redemptionCustomerId)
                .redemptionItemId(redemptionItemId)
                .redemptionOrderId(redemptionOrderId)
                .walletCodeApplied(walletCodeApplied)
                .build();
    }
}
