package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.GiftCard;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class GiftCardDTO {

    private String cardNumber;
    private String cardNickname;

    public static GiftCardDTO fromEntity(GiftCard entity) {
        return DTOConverter.convert(new GiftCardDTO(), entity);
    }

    public GiftCard toEntity() {
        GiftCard giftCard = new GiftCard();
        giftCard.setCardNumber(cardNumber);
        giftCard.setCardNickname(cardNickname);

        return giftCard;
    }
}
