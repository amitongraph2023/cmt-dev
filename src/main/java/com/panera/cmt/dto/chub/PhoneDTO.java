package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.panera.cmt.dto.proxy.chub.Phone;
import com.panera.cmt.serializer.RigidBooleanDeserializer;
import com.panera.cmt.util.DTOConverter;
import lombok.Data;

@Data
public class PhoneDTO {

    private Long id;
    private String phoneNumber;
    private String phoneType;
    private String countryCode;
    private String extension;
    private String name;

    @JsonProperty
    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    private boolean isCallOpt;

    @JsonProperty
    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    private boolean isDefault;

    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isValid;

    // Getters
    @JsonIgnore
    public boolean isCallOpt() {
        return isCallOpt;
    }
    @JsonIgnore
    public boolean isDefault() {
        return isDefault;
    }
    @JsonIgnore
    public boolean isValid() {
        return isValid;
    }

    // Setters
    @JsonIgnore
    public void setCallOpt(boolean callOpt) {
        isCallOpt = callOpt;
    }
    @JsonIgnore
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    @JsonIgnore
    public void setValid(boolean valid) {
        isValid = valid;
    }

    public static PhoneDTO fromEntity(Phone entity) {
        return DTOConverter.convert(new PhoneDTO(), entity);
    }

    public Phone toEntity() {
        Phone phone = new Phone();
        phone.setPhoneNumber(phoneNumber);
        phone.setPhoneType(phoneType);
        phone.setCountryCode(countryCode);
        phone.setExtension(extension);
        phone.setName(name);
        phone.setCallOpt(isCallOpt);
        phone.setDefault(isDefault);

        return phone;
    }
}
