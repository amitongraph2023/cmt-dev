package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panera.cmt.dto.proxy.chub.CorporateCateringAccount;
import com.panera.cmt.serializer.DateTimeDeserializer;
import com.panera.cmt.serializer.DateTimeSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class CorporateCateringAccountDTO {

    private Long orgNumber;
    private String ccaNumber;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date clientStartDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date clientEndDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date orgStartDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date orgEndDate;

    private String ccaBillingName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Boolean poRequired;
    private Boolean onlineEnabled;

    public static CorporateCateringAccountDTO fromEntity(CorporateCateringAccount entity) {
        if (entity == null) {
            return null;
        }

        CorporateCateringAccountDTO dto = new CorporateCateringAccountDTO();
        dto.setOrgNumber(entity.getOrgNumber());
        dto.setCcaNumber(entity.getCcaNumber());
        dto.setClientStartDate(entity.getClientStartDate());
        dto.setClientEndDate(entity.getClientEndDate());
        dto.setOrgStartDate(entity.getOrgStartDate());
        dto.setOrgEndDate(entity.getOrgEndDate());
        dto.setCcaBillingName(entity.getCcaBillingName());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setZipCode(entity.getZipCode());
        dto.setCountry(entity.getCountry());
        dto.setPoRequired(entity.getPoRequired());
        dto.setOnlineEnabled(entity.getOnlineEnabled());

        return dto;
    }
}
