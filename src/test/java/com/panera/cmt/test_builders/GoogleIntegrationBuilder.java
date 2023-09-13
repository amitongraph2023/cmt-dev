package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.GoogleIntegration;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class GoogleIntegrationBuilder extends BaseObjectBuilder<GoogleIntegration> {

    private String googleId = randomAlphanumeric(15);

    @Override
    GoogleIntegration getTestClass() {
        return new GoogleIntegration();
    }
}
