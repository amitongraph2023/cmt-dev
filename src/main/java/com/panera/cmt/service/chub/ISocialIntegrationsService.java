package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.SocialIntegrations;

import java.util.List;
import java.util.Optional;

public interface ISocialIntegrationsService {
    Optional<SocialIntegrations> getSocialIntegrations(Long customerId);
}
