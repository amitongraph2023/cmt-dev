package com.panera.cmt.dto.proxy.chub;

import lombok.Data;

@Data
public class SocialIntegrations {

    private AppleIntegration appleIntegration;
    private FacebookIntegration facebookIntegration;
    private GoogleIntegration googleIntegration;
}
