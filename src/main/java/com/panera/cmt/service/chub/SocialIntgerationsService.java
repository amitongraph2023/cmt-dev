package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.SocialIntegrations;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_SOCIAL_INTEGRATIONS;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class SocialIntgerationsService extends BaseCustomerHubService implements ISocialIntegrationsService {

    @Override
    public Optional<SocialIntegrations> getSocialIntegrations(Long customerId) {
        if (isNull(customerId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getSocialIntegrations", String.format("Getting social integrations for customerId=%d", customerId));

        return Optional.ofNullable(doGet(SocialIntegrations.class, stopWatch, ChubEndpoints.SOCIAL_INTEGRATIONS_BASE, customerId));

    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_SOCIAL_INTEGRATIONS;
    }
}
