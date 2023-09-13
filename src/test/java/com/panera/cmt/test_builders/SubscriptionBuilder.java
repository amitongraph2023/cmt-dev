package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.Subscription;

import java.util.Random;

public class SubscriptionBuilder extends BaseObjectBuilder<Subscription> {

    private Integer subscriptionCode = 1;
    private String displayName = "Reward Reminders & Expiration Alerts";
    private boolean isSubscribed = new Random().nextBoolean();

    public SubscriptionBuilder withSubscriptionCode(Integer subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
        return this;
    }

    public SubscriptionBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SubscriptionBuilder withSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
        return this;
    }

    @Override
    Subscription getTestClass() {
        return new Subscription();
    }
}
