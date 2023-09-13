package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.GiftCard;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class GiftCardBuilder extends BaseObjectBuilder<GiftCard> {

    private String cardNumber = randomNumeric(18);
    private String cardNickname = UUID.randomUUID().toString();

    @Override
    GiftCard getTestClass() {
        return new GiftCard();
    }
}
