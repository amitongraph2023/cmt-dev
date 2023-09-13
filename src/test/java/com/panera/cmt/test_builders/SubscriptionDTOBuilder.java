package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.SubscriptionDTO;

import java.util.Random;

public class SubscriptionDTOBuilder extends BaseObjectBuilder<SubscriptionDTO> {

    private Integer subscriptionCode = 1;
    private String displayName = "Reward Reminders & Expiration Alerts";
    private boolean isSubscribed = new Random().nextBoolean();

    public SubscriptionDTOBuilder withSubscriptionCode(Integer subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
        return this;
    }

    public SubscriptionDTOBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SubscriptionDTOBuilder withSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
        return this;
    }

    @Override
    SubscriptionDTO getTestClass() {
        return new SubscriptionDTO();
    }
}
