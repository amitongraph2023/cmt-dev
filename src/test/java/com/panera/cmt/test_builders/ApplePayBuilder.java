package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.ApplePay;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class ApplePayBuilder extends BaseObjectBuilder<ApplePay> {

    String accountNumber = randomNumeric(18);

    @Override
    ApplePay getTestClass() {
        return new ApplePay();
    }
}
