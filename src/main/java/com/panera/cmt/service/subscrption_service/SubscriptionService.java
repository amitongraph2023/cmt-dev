package com.panera.cmt.service.subscrption_service;

import com.panera.cmt.dto.proxy.subscription_service.CoffeeSubscriptionUsage;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionPrograms;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionServiceGiftCoffeeResults;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.GiftCoffeeSearchType;
import com.panera.cmt.enums.SubscriptionServiceEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.panera.cmt.util.SharedUtils.isNull;
import static java.util.Arrays.asList;

@Slf4j
@Service
public class SubscriptionService extends BaseSubscriptionService implements ISubscriptionService {

    @Override
    public Optional<SubscriptionServiceGiftCoffeeResults> getGiftCoffeeSubscriptions(GiftCoffeeSearchType searchType, String searchTerm) {
        if (isNull(searchTerm, searchType)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getGiftCoffeeSubscriptions", String.format("Getting Gift Coffee Subscriptions for searchType=%s for searchTerm=%s", searchType, searchTerm));

        return Optional.ofNullable(doGet(SubscriptionServiceGiftCoffeeResults.class, stopWatch, SubscriptionServiceEndpoints.GIFT_BASE, searchType.getType(), searchTerm));
    }

    @Override
    public Optional<ResponseHolder<Boolean>> cancelCoffeeSubscriotion(Long customerId, Long programId) {
        if (isNull(customerId) || isNull(programId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "cancelCoffeeSubscription", String.format("Cancel Coffee Subscription for customerId=%d", customerId));

        return Optional.of(doPost(Boolean.class, stopWatch, createAudit(ActionType.DELETE, customerId, null), "{\n" +
                "  \"body\": {},\n" +
                "  \"status\": {}\n" +
                "}", SubscriptionServiceEndpoints.CANCEL, customerId, programId));

    }

    @Override
    public Optional<SubscriptionPrograms> getSubscriptionId(Long customerId, Long programId) {
        if (isNull(customerId) || isNull(programId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getSubscriptionId", String.format("Getting Subscription for customerId=%d for programId=%d", customerId, programId));

        return Optional.ofNullable(doGet(SubscriptionPrograms.class, stopWatch, SubscriptionServiceEndpoints.SUBSCRIPTION_PROGRAMS, customerId, programId));

    }


    @Override
    public Optional<List<CoffeeSubscriptionUsage>> getCustomerCoffeeSubscriptionUsage(Long customerId) {
        if (isNull(customerId)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getCoffeeSubscriptionUsage", String.format("Getting Coffee Subscription Usage for customerId=%d", customerId));

        CoffeeSubscriptionUsage[] coffeeSubscriptionUsages = doGet(CoffeeSubscriptionUsage[].class, stopWatch, SubscriptionServiceEndpoints.USAGE, customerId);

        return Optional.of((coffeeSubscriptionUsages != null) ? asList(coffeeSubscriptionUsages) : new ArrayList<>());
    }

    @Override
    protected String getSubjectName() {
        return null;
    }
}
