package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.dto.proxy.chub.Customer;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class CustomerDTO {

    private Long customerId;
    private String username;
    private String firstName;
    private String lastName;
    @JsonProperty private boolean isEmailGlobalOpt;
    @JsonProperty private boolean isSmsGlobalOpt;
    @JsonProperty private boolean isMobilePushOpt;
    @JsonProperty private Boolean isDoNotShare;
    
    // Getters
    @JsonIgnore
    public boolean isEmailGlobalOpt() {
        return isEmailGlobalOpt;
    }
    @JsonIgnore
    public boolean isSmsGlobalOpt() {
        return isSmsGlobalOpt;
    }
    @JsonIgnore
    public boolean isMobilePushOpt() {
        return isMobilePushOpt;
    }
    
    @JsonIgnore
    public Boolean isDoNotShare() {
    return isDoNotShare;
    }

    // Setters
    @JsonIgnore
    public void setEmailGlobalOpt(boolean emailGlobalOpt) {
        isEmailGlobalOpt = emailGlobalOpt;
    }
    @JsonIgnore
    public void setSmsGlobalOpt(boolean smsGlobalOpt) {
        isSmsGlobalOpt = smsGlobalOpt;
    }
    @JsonIgnore
    public void setMobilePushOpt(boolean mobilePushOpt) {
        isMobilePushOpt = mobilePushOpt;
    }
    
    @JsonIgnore
    public void setDoNotShare(Boolean doNotShare) {
    this.isDoNotShare = doNotShare;
    }

    public static CustomerDTO fromEntity(Customer entity) {
        return DTOConverter.convert(new CustomerDTO(), entity);
    }
    
    public Customer toEntity() {
        Customer entity = new Customer();
        entity.setCustomerId(customerId);
        entity.setUsername(username);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setEmailGlobalOpt(isEmailGlobalOpt);
        entity.setSmsGlobalOpt(isSmsGlobalOpt);
        entity.setMobilePushOpt(isMobilePushOpt);

        return entity;
    }
}
