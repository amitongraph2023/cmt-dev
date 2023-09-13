package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.GiftCardDTO;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class GiftCardDTOBuilder extends BaseObjectBuilder<GiftCardDTO> {

    private String cardNumber = randomNumeric(18);
    private String cardNickname = UUID.randomUUID().toString();

    @Override
    GiftCardDTO getTestClass() {
        return new GiftCardDTO();
    }
}
