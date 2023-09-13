package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.CardExchange;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class CardExchangeDTO {
    private String cardNumber;
    private boolean opt;
    private String regCode;

    public static CardExchangeDTO fromEntity(CardExchange entity) {
        return DTOConverter.convert(new CardExchangeDTO(), entity);
    }

    public CardExchange toEntity() {
        CardExchange entity = new CardExchange();
        entity.setCardNumber(this.getCardNumber());
        entity.setOpt(this.isOpt());
        entity.setRegCode(this.getRegCode());

        return entity;
    }
}

