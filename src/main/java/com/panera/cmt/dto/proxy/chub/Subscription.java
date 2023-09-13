package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.panera.cmt.serializer.RigidBooleanDeserializer;
import lombok.Data;

@Data
public class Subscription {

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
}
