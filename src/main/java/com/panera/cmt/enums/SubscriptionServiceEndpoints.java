package com.panera.cmt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SubscriptionServiceEndpoints {
    CANCEL("/subscriptions/{customerId}/unsubscribe/{programId}?removeOptIn=true", "/subscriptions/{customerId}/unsubscribe/{programId}?removeOptIn=true&env={env}"),
    GIFT_BASE("/subscriptions/gift/search?{searchType}={searchTerm}", "/subscriptions/gift/search?{searchType}={searchTerm}&env={env}"),
    SUBSCRIPTION_PROGRAMS("/subscriptions/customer/{customerId}/programs/{programId}", "/subscriptions/customer/{customerId}/programs/{programId}?env={env}"),
    USAGE("/subscriptions/customer/{customerId}/transactions", "/subscriptions/customer/{customerId}/transactions?env={env}");

    private final String stub;
    private final String stubWithEnv;
}
