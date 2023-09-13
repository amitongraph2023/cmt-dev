package com.panera.cmt.dto.proxy.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerDetails extends Customer {

    private Loyalty loyalty;
    private BirthDate birthDate;
    private SocialIntegrations socialIntegration;
    private List<TaxExemption> taxExemptions;
    private String status;
    private String regCampaign;
    private String regReferrer;
    private  String accountCreationDate;

    @JsonProperty
    private boolean isSuspended;

    // Getters
    public String getLoyaltyCardNumber() {

        return (loyalty == null) ? null : loyalty.getCardNumber();
    }
    public String getDob() {
        return (birthDate == null) ? null : birthDate.getBirthDate();
    }
    public List<TaxExemption> getTaxExemptions() {

        return (taxExemptions != null) ? taxExemptions : new ArrayList<>();
    }
    @JsonIgnore
    public boolean isSuspended() {

        return isSuspended;
    }

    // Setters
    @JsonIgnore
    public void setSuspended(boolean suspended) {

        isSuspended = suspended;
    }
}
