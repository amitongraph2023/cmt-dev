package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.panera.cmt.dto.proxy.chub.Email;
import com.panera.cmt.serializer.RigidBooleanDeserializer;
import com.panera.cmt.util.DTOConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailDTO {

    private Long id;
    private String emailAddress;
    private String emailType;

    @JsonProperty
    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    private boolean isDefault;

    @JsonProperty
    @JsonDeserialize(using = RigidBooleanDeserializer.class)
    private boolean isOpt;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isVerified;

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

    public static EmailDTO fromEntity(Email entity) {
        return DTOConverter.convert(new EmailDTO(), entity);
    }

    public Email toEntity() {
        Email email = new Email();
        email.setId(id);
        email.setEmailAddress(emailAddress);
        email.setEmailType(emailType);
        email.setDefault(isDefault);
        email.setOpt(isOpt);
        email.setVerified(isVerified);

        return email;
    }
}
