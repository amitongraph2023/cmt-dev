package com.panera.cmt.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.serializer.DateTimeDeserializer;
import com.panera.cmt.serializer.DateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@ApiModel(value="AuthenticationToken", description="SSO session information")
@Data
public class AuthenticationTokenDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String accessToken;
    private String role;
    private String displayName;
    private String knownAs;    

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date loginDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date expirationDate;

    public static AuthenticationTokenDTO fromEntity(AuthenticatedUser entity) {
        if (entity == null) {
            return null;
        }

        AuthenticationTokenDTO dto = new AuthenticationTokenDTO();
        dto.setUsername(entity.getUsername());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmailAddress(entity.getEmailAddress());
        dto.setAccessToken(entity.getAccessToken());
        if (entity.getRole() != null) {
            dto.setRole(entity.getRole().name());
        }
        dto.setLoginDate(entity.getLoginDate());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setDisplayName(entity.getDisplayName());
        dto.setKnownAs(entity.getKnownAs());
        return dto;
    }
}
