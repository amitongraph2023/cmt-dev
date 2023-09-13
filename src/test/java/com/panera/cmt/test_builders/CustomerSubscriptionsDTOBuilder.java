package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.CustomerSubscriptionsDTO;
import com.panera.cmt.dto.chub.SubscriptionDTO;
import com.panera.cmt.dto.chub.SubscriptionSuppressorDTO;

import java.util.List;

import static java.util.Collections.singletonList;

public class CustomerSubscriptionsDTOBuilder extends BaseObjectBuilder<CustomerSubscriptionsDTO> {

    private List<SubscriptionDTO> subscriptions = singletonList(new SubscriptionDTOBuilder().build());
    private List<SubscriptionSuppressorDTO> suppressors = singletonList(new SubscriptionSuppressorDTOBuilder().build());

    public CustomerSubscriptionsDTOBuilder withSubscriptions(List<SubscriptionDTO> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    public CustomerSubscriptionsDTOBuilder withSubscription(SubscriptionDTO subscription) {
        this.subscriptions = singletonList(subscription);
        return this;
    }

    public CustomerSubscriptionsDTOBuilder withSuppressors(List<SubscriptionSuppressorDTO> suppressors) {
        this.suppressors = suppressors;
        return this;
    }

    public CustomerSubscriptionsDTOBuilder withSuppressor(SubscriptionSuppressorDTO suppressor) {
        this.suppressors = singletonList(suppressor);
        return this;
    }

    @Override
    CustomerSubscriptionsDTO getTestClass() {
        return new CustomerSubscriptionsDTO();
    }
}
