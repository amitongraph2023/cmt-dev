package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.PayPalDTO;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class PayPalDTOBuilder extends BaseObjectBuilder<PayPalDTO> {

    String accountNumber = randomNumeric(18);
    String username = UUID.randomUUID().toString();

    @Override
    PayPalDTO getTestClass() {
        return new PayPalDTO();
    }
}
