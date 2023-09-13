package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.dto.proxy.chub.CreditCard;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class CreditCardDTO {

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

    public static CreditCardDTO fromEntity(CreditCard entity) {
        return DTOConverter.convert(new CreditCardDTO(), entity);
    }

    public CreditCard toEntity() {
        CreditCard entity = new CreditCard();
        entity.setToken(token);
        entity.setExpirationDate(expirationDate);
        entity.setCardholderName(cardholderName);
        entity.setLastFour(lastFour);
        entity.setCreditCardType(creditCardType);
        entity.setPaymentProcessor(paymentProcessor);
        entity.setPaymentLabel(paymentLabel);
        entity.setIsDefault(isDefault);
        entity.setCreditCardZip(creditCardZip);
        entity.setIsDefaultSubscription(isDefaultSubscription);
        return entity;
    }
}
