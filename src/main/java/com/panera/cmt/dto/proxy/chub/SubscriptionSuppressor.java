package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.panera.cmt.serializer.RigidBooleanDeserializer;
import lombok.Data;

@Data
public class SubscriptionSuppressor {

    private Integer suppressionCode;
    private String displayName;

    @JsonProperty
    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    private boolean isSuppressed;

    // Getters
    @JsonIgnore public boolean isSuppressed() {
        return isSuppressed;
    }

    // Setters
    @JsonIgnore public void setSuppressed(boolean suppressed) {
        isSuppressed = suppressed;
    }
}
