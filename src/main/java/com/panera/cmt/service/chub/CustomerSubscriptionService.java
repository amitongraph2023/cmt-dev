package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.CustomerSubscriptionsDTO;
import com.panera.cmt.dto.proxy.chub.CustomerSubscriptions;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_SUBSCRIPTIONS;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class CustomerSubscriptionService extends BaseCustomerHubService implements ICustomerSubscriptionService {

    @Override
    public Optional<CustomerSubscriptions> getSubscriptions(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getSubscriptions", String.format("Getting subscriptions for customerId=%d", customerId));

        return Optional.ofNullable(doGet(CustomerSubscriptions.class, stopWatch, ChubEndpoints.SUBSCRIPTIONS, customerId));
    }

    @Override
    public Optional<ResponseHolder<CustomerSubscriptions>> updateSubscriptions(Long customerId, CustomerSubscriptions subscriptions) {
        if (isNull(customerId, subscriptions)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateSubscriptions", String.format("Updating subscriptions for customerId=%d", customerId));

        return Optional.ofNullable(doPut(CustomerSubscriptions.class, stopWatch, createAudit(ActionType.UPDATE, customerId, subscriptions), CustomerSubscriptionsDTO.fromEntity(subscriptions), ChubEndpoints.SUBSCRIPTIONS, customerId));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_SUBSCRIPTIONS;
    }
}
