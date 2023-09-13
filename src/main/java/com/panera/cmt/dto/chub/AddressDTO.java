package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.dto.proxy.chub.Address;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class AddressDTO {

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

    public static AddressDTO fromEntity(Address entity) {
        return DTOConverter.convert(new AddressDTO(), entity);
    }

    public Address toEntity() {
        Address entity = new Address();
        entity.setName(name);
        entity.setContactPhone(contactPhone);
        entity.setPhoneExtension(phoneExtension);
        entity.setAdditionalInfo(additionalInfo);
        entity.setAddressLine1(addressLine1);
        entity.setAddressLine2(addressLine2);
        entity.setCity(city);
        entity.setState(state);
        entity.setCountry(country);
        entity.setZip(zip);
        entity.setAddressType(addressType);
        entity.setDefault(isDefault);

        return entity;
    }
}
