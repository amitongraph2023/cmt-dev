package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.CustomerSubscriptions;
import com.panera.cmt.dto.proxy.chub.Subscription;
import com.panera.cmt.dto.proxy.chub.SubscriptionSuppressor;

import java.util.List;

import static java.util.Collections.singletonList;

public class CustomerSubscriptionsBuilder extends BaseObjectBuilder<CustomerSubscriptions> {

    private List<Subscription> subscriptions = singletonList(new SubscriptionBuilder().build());
    private List<SubscriptionSuppressor> suppressors = singletonList(new SubscriptionSuppressorBuilder().build());

    public CustomerSubscriptionsBuilder withSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    public CustomerSubscriptionsBuilder withSubscription(Subscription subscription) {
        this.subscriptions = singletonList(subscription);
        return this;
    }

    public CustomerSubscriptionsBuilder withSuppressors(List<SubscriptionSuppressor> suppressors) {
        this.suppressors = suppressors;
        return this;
    }

    public CustomerSubscriptionsBuilder withSuppressor(SubscriptionSuppressor suppressor) {
        this.suppressors = singletonList(suppressor);
        return this;
    }

    @Override
    CustomerSubscriptions getTestClass() {
        return new CustomerSubscriptions();
    }
}
