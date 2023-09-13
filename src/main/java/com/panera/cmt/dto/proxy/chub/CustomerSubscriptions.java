package com.panera.cmt.dto.proxy.chub;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerSubscriptions {

    private List<Subscription> subscriptions;
    private List<SubscriptionSuppressor> suppressors;

    // Getters
    public List<Subscription> getSubscriptions() {
        return (subscriptions != null) ? subscriptions : new ArrayList<>();
    }
    public List<SubscriptionSuppressor> getSuppressors() {
        return (suppressors != null) ? suppressors : new ArrayList<>();
    }

    // Setters
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
    public void setSuppressors(List<SubscriptionSuppressor> suppressors) {
        this.suppressors = suppressors;
    }
}
