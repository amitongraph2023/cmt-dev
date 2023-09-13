package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Email {

    private Long id;
    private String emailAddress;
    private String emailType;
    @JsonProperty private boolean isDefault;
    @JsonProperty private boolean isOpt;
    @JsonProperty private boolean isVerified;

    // Getters
    @JsonIgnore public boolean isDefault() {
        return isDefault;
    }
    @JsonIgnore public boolean isOpt() {
        return isOpt;
    }
    @JsonIgnore public boolean isVerified() {
        return isVerified;
    }

    // Setters
    @JsonIgnore public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
    @JsonIgnore public void setOpt(Boolean opt) {
        isOpt = opt;
    }
    @JsonIgnore public void setVerified(Boolean verified) {
        isVerified = verified;
    }
}
