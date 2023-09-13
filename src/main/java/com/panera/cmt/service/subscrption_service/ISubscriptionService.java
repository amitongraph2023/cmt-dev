package com.panera.cmt.service.subscrption_service;

import com.panera.cmt.dto.proxy.subscription_service.CoffeeSubscriptionUsage;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionPrograms;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionServiceCoffeeUsageResults;
import com.panera.cmt.dto.proxy.subscription_service.SubscriptionServiceGiftCoffeeResults;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.GiftCoffeeSearchType;

import java.util.List;
import java.util.Optional;

public interface ISubscriptionService {

    Optional<List<CoffeeSubscriptionUsage>> getCustomerCoffeeSubscriptionUsage(Long customerId);

    Optional<SubscriptionServiceGiftCoffeeResults> getGiftCoffeeSubscriptions(GiftCoffeeSearchType searchType, String searchTerm);

    Optional<ResponseHolder<Boolean>> cancelCoffeeSubscriotion(Long customerId, Long programId);

    Optional<SubscriptionPrograms> getSubscriptionId(Long customerId, Long programId);

}
