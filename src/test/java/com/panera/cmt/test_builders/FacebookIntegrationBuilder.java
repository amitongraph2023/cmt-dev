package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.FacebookIntegration;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class FacebookIntegrationBuilder extends BaseObjectBuilder<FacebookIntegration> {

    private String facebookId = randomAlphanumeric(15);
    
    @Override
    FacebookIntegration getTestClass() {
        return new FacebookIntegration();
    }
}
