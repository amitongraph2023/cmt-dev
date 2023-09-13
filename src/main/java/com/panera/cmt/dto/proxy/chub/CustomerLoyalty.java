package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerLoyalty {

	private String cardNumber;
	private String accountStatus;
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
}
