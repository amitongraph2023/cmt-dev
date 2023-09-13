package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.CustomerSubscriptions;
import com.panera.cmt.entity.ResponseHolder;

import java.util.Optional;

public interface ICustomerSubscriptionService {

    Optional<CustomerSubscriptions> getSubscriptions(Long customerId);

    Optional<ResponseHolder<CustomerSubscriptions>> updateSubscriptions(Long customerId, CustomerSubscriptions subscriptions);
}
