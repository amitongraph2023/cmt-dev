package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.dto.proxy.chub.CustomerLoyalty;
import com.panera.cmt.util.DTOConverter;

import lombok.Data;

@Data
public class LoyaltyDTO {
    private String accountStatus;
    private String cardNumber;
    @JsonProperty private boolean isRewardsEnabled;

    // Getters
    @JsonIgnore
    public boolean isRewardsEnabled() {
        return isRewardsEnabled;
    }

    // Setters
    @JsonIgnore
    public void setRewardsEnabled(boolean isRewardsEnabled) {
        this.isRewardsEnabled = isRewardsEnabled;
    }

    public static LoyaltyDTO fromEntity(CustomerLoyalty entity) {
        return DTOConverter.convert(new LoyaltyDTO(), entity);
    }
}
