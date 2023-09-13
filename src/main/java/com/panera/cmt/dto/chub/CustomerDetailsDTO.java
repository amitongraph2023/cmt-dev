package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.dto.proxy.chub.CustomerDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerDetailsDTO extends CustomerDTO {

    private String loyaltyCardNumber;
    private String dob;
    private String status;
    private SocialIntegrationsDTO socialIntegration;
    private List<TaxExemptionDTO> taxExemptions;
    private String accountCreationDate;
    @JsonProperty
    private boolean isSuspended;

    private String regCampaign;
    private String regReferrer;

    // Getters
    @JsonIgnore
    public boolean isSuspended() {
        return isSuspended;
    }

    // Setters
    @JsonIgnore
    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public static CustomerDetailsDTO fromEntity(CustomerDetails entity) {
        return fromEntity(entity, true);
    }

    public static CustomerDetailsDTO fromEntity(CustomerDetails entity, boolean showAll) {
        if (entity == null) {
            return null;
        }

        CustomerDetailsDTO dto = new CustomerDetailsDTO();
        dto.setCustomerId(entity.getCustomerId());
        dto.setUsername(entity.getUsername());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setStatus(entity.getStatus());
        dto.setEmailGlobalOpt(entity.isEmailGlobalOpt());
        dto.setIsDoNotShare(entity.isDoNotShare());
        dto.setSmsGlobalOpt(entity.isSmsGlobalOpt());
        dto.setMobilePushOpt(entity.isMobilePushOpt());
        dto.setLoyaltyCardNumber(entity.getLoyaltyCardNumber());
        dto.setAccountCreationDate(entity.getAccountCreationDate());
        dto.setSuspended(entity.isSuspended());
        dto.setRegCampaign(entity.getRegCampaign());
        dto.setRegReferrer(entity.getRegReferrer());

        if (showAll) {
            dto.setDob(entity.getDob());
            dto.setSocialIntegration(SocialIntegrationsDTO.fromEntity(entity.getSocialIntegration()));
            dto.setTaxExemptions(entity.getTaxExemptions().stream()
                    .map(TaxExemptionDTO::fromEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
