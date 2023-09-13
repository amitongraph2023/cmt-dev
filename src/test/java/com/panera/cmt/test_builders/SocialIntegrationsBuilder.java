package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.FacebookIntegration;
import com.panera.cmt.dto.proxy.chub.GoogleIntegration;
import com.panera.cmt.dto.proxy.chub.SocialIntegrations;

public class SocialIntegrationsBuilder extends BaseObjectBuilder<SocialIntegrations> {

    private FacebookIntegration facebookIntegration = new FacebookIntegrationBuilder().build();
    private GoogleIntegration googleIntegration = new GoogleIntegrationBuilder().build();

    @Override
    SocialIntegrations getTestClass() {
        return new SocialIntegrations();
    }
}
