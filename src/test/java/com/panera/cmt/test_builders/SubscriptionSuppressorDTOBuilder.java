package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.SubscriptionSuppressorDTO;

import java.util.Random;

public class SubscriptionSuppressorDTOBuilder extends BaseObjectBuilder<SubscriptionSuppressorDTO> {

    private Integer suppressionCode = 1;
    private String displayName = "Catering";
    private boolean isSuppressed = new Random().nextBoolean();

    public SubscriptionSuppressorDTOBuilder withSuppressionCode(Integer suppressionCode) {
        this.suppressionCode = suppressionCode;
        return this;
    }

    public SubscriptionSuppressorDTOBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SubscriptionSuppressorDTOBuilder withSuppressed(boolean suppressed) {
        isSuppressed = suppressed;
        return this;
    }

    @Override
    SubscriptionSuppressorDTO getTestClass() {
        return new SubscriptionSuppressorDTO();
    }
}
