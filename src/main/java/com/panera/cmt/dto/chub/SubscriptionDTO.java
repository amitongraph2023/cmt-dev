package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.panera.cmt.dto.proxy.chub.Subscription;
import com.panera.cmt.serializer.RigidBooleanDeserializer;
import lombok.Data;

@Data
public class SubscriptionDTO {

    private Integer subscriptionCode;
    private String displayName;

    @JsonProperty
    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    private boolean isSubscribed;

    // Getters
    @JsonIgnore public boolean isSubscribed() {
        return isSubscribed;
    }

    // Setters
    @JsonIgnore public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public static SubscriptionDTO fromEntity(Subscription entity) {
        if (entity == null) {
            return null;
        }

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setSubscriptionCode(entity.getSubscriptionCode());
        dto.setDisplayName(entity.getDisplayName());
        dto.setSubscribed(entity.isSubscribed());
        return dto;
    }

    public Subscription toEntity() {
        Subscription entity = new Subscription();
        entity.setSubscriptionCode(subscriptionCode);
        entity.setDisplayName(displayName);
        entity.setSubscribed(isSubscribed);
        return entity;
    }
}
