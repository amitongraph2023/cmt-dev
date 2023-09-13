package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.SubscriptionSuppressor;

import java.util.Random;

public class SubscriptionSuppressorBuilder extends BaseObjectBuilder<SubscriptionSuppressor> {

    private Integer suppressionCode = 1;
    private String displayName = "Catering";
    private boolean isSuppressed = new Random().nextBoolean();

    public SubscriptionSuppressorBuilder withSuppressionCode(Integer suppressionCode) {
        this.suppressionCode = suppressionCode;
        return this;
    }

    public SubscriptionSuppressorBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SubscriptionSuppressorBuilder withSuppressed(boolean suppressed) {
        isSuppressed = suppressed;
        return this;
    }

    @Override
    SubscriptionSuppressor getTestClass() {
        return new SubscriptionSuppressor();
    }
}
