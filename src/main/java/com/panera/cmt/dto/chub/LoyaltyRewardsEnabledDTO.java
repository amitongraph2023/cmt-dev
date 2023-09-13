package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoyaltyRewardsEnabledDTO {

	@JsonProperty
	private boolean isRewardsEnabled;

	@JsonIgnore
	public boolean isRewardsEnabled() {
		return isRewardsEnabled;
	}

	@JsonIgnore
	public void setRewardsEnabled(boolean isRewardsEnabled) {
		this.isRewardsEnabled = isRewardsEnabled;
	}

}
