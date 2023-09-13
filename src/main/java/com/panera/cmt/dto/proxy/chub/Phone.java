package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Phone {

    private Long id;
    private String phoneNumber;
    private String phoneType;
    private String countryCode;
    private String extension;
    private String name;
    @JsonProperty private boolean isCallOpt;
    @JsonProperty private boolean isDefault;
    @JsonProperty private boolean isValid = false;

    // Getters
    @JsonIgnore public boolean isCallOpt() {
        return isCallOpt;
    }
    @JsonIgnore public boolean isDefault() {
        return isDefault;
    }
    @JsonIgnore public boolean isValid() {
        return isValid;
    }

    // Setters
    @JsonIgnore public void setCallOpt(boolean callOpt) {
        isCallOpt = callOpt;
    }
    @JsonIgnore public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    @JsonIgnore public void setValid(boolean valid) {
        isValid = valid;
    }
}
