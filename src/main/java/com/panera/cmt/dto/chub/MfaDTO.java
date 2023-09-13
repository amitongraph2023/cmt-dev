package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.dto.proxy.chub.Mfa;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class MfaDTO {
    @JsonProperty
    private Boolean isDefaultMfaType;

    @JsonProperty
    private Boolean isEnabled;

    private String type;


    // Getters
    @JsonIgnore
    public boolean isDefaultMfaType() {
        return isDefaultMfaType;
    }

    @JsonIgnore
    public boolean isEnabled() { return isEnabled; }

    // Setters
    @JsonIgnore public void setIsDefaultMfaType(boolean isDefaultMfaType) {
        this.isDefaultMfaType = isDefaultMfaType;
    }

    @JsonIgnore public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }


    public static MfaDTO fromEntity(Mfa entity) {

        return DTOConverter.convert(new MfaDTO(), entity);
    }

    public Mfa toEntity() {
        Mfa entity = new Mfa();
        entity.setIsDefaultMfaType(isDefaultMfaType);
        entity.setIsEnabled(isEnabled);
        entity.setType(type);

        return entity;
    }
}
