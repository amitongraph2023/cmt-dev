package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.PayPal;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class PayPalBuilder extends BaseObjectBuilder<PayPal> {

    String accountNumber = randomNumeric(18);
    String username = UUID.randomUUID().toString();

    @Override
    PayPal getTestClass() {
        return new PayPal();
    }
}
