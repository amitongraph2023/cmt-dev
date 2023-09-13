package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreditCard {

    private String token;
    private String expirationDate;
    private String cardholderName;
    private String creditCardZip;
    private String lastFour;
    private String creditCardType;
    private String paymentProcessor;
    private String paymentLabel;

    @JsonProperty private boolean isDefault;

    @JsonProperty private boolean isDefaultSubscription;

    // Getters
    @JsonIgnore public boolean isDefault() {
        return isDefault;
    }

    // Setters
    @JsonIgnore public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Getters
    @JsonIgnore public boolean isDefaultSubscription() {
        return isDefaultSubscription;
    }

    // Setters
    @JsonIgnore public void setIsDefaultSubscription(boolean isDefaultSubscription) {
        this.isDefaultSubscription = isDefaultSubscription;
    }

}
