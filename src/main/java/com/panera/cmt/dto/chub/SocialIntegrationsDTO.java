package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.SocialIntegrations;
import lombok.Data;

@Data
public class SocialIntegrationsDTO {

    private AppleIntegrationDTO appleIntegration;
    private FacebookIntegrationDTO facebookIntegration;
    private GoogleIntegrationDTO googleIntegration;

    public static SocialIntegrationsDTO fromEntity(SocialIntegrations entity) {
        if (entity == null) {
            return null;
        }

        SocialIntegrationsDTO dto = new SocialIntegrationsDTO();
        dto.setFacebookIntegration(FacebookIntegrationDTO.fromEntity(entity.getFacebookIntegration()));
        dto.setGoogleIntegration(GoogleIntegrationDTO.fromEntity(entity.getGoogleIntegration()));
        dto.setAppleIntegration(AppleIntegrationDTO.fromEntity(entity.getAppleIntegration()));

        return dto;
    }
}
