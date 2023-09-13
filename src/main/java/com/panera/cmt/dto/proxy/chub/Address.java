package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address {

    private Long id;
    private String name;
    private String contactPhone;
    private String phoneExtension;
    private String additionalInfo;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String addressType;
    @JsonProperty private boolean isDefault;

    // Getters
    @JsonIgnore public boolean isDefault() {
        return isDefault;
    }

    // Setters
    @JsonIgnore public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
