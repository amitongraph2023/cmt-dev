package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.CampusCard;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class CampusCardDTO {
    private String campusCardToken;
    private String institutionLongName;
    private String institutionShortName;
    private String providerName;
    private String programName;
    private String customerCampusCardName;
    private String expirationDate;
    private String postalCode;


    public static CampusCardDTO fromEntity(CampusCard entity) {
        return DTOConverter.convert(new CampusCardDTO(), entity);
    }

    public CampusCard toEntity() {
        CampusCard entity = new CampusCard();
        entity.setCampusCardToken(this.getCampusCardToken());
        entity.setCustomerCampusCardName(this.getCustomerCampusCardName());
        entity.setInstitutionLongName(this.getInstitutionLongName());
        entity.setInstitutionShortName(this.getInstitutionShortName());
        entity.setProgramName(this.getProgramName());
        entity.setProviderName(this.getProviderName());
        entity.setExpirationDate(this.getExpirationDate());
        entity.setPostalCode(this.getPostalCode());
        return entity;
    }
}
