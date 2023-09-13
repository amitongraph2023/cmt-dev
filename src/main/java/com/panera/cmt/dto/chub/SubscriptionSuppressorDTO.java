package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.panera.cmt.dto.proxy.chub.SubscriptionSuppressor;
import com.panera.cmt.serializer.RigidBooleanDeserializer;
import lombok.Data;

@Data
public class SubscriptionSuppressorDTO {

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

    public static SubscriptionSuppressorDTO fromEntity(SubscriptionSuppressor entity) {
        if (entity == null) {
            return null;
        }

        SubscriptionSuppressorDTO dto = new SubscriptionSuppressorDTO();
        dto.setSuppressionCode(entity.getSuppressionCode());
        dto.setDisplayName(entity.getDisplayName());
        dto.setSuppressed(entity.isSuppressed());
        return dto;
    }

    public SubscriptionSuppressor toEntity() {
        SubscriptionSuppressor entity = new SubscriptionSuppressor();
        entity.setSuppressionCode(suppressionCode);
        entity.setDisplayName(displayName);
        entity.setSuppressed(isSuppressed);
        return entity;
    }
}
