package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Mfa {

    @JsonProperty
    private boolean isDefaultMfaType;

    @JsonProperty
    private Boolean isEnabled;

    private String type;

    // Getters
    @JsonIgnore
    public boolean isDefaultMfaType() { return isDefaultMfaType;}

    @JsonIgnore
    public boolean isEnabled() { return isEnabled; }

    // Setters
    @JsonIgnore public void setIsDefaultMfaType(boolean isDefaultMfaType) {
        this.isDefaultMfaType = isDefaultMfaType;
    }

    @JsonIgnore public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
}