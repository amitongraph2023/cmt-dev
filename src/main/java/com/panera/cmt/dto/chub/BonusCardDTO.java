package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.BonusCard;
import com.panera.cmt.util.DTOConverter;

import lombok.Data;

@Data
public class BonusCardDTO {

    private String cardNumber;
    private String cardNickname;
    private String effectiveStartDate;
    private String effectiveEndDate;

    public static BonusCardDTO fromEntity(BonusCard entity) {
        return DTOConverter.convert(new BonusCardDTO(), entity);
    }

    public BonusCard toEntity() {
        BonusCard bonusCard = new BonusCard();
        bonusCard.setCardNumber(cardNumber);
        bonusCard.setCardNickname(cardNickname);
        bonusCard.setEffectiveStartDate(effectiveStartDate);
        bonusCard.setEffectiveEndDate(effectiveEndDate);

        return bonusCard;
    }
}

