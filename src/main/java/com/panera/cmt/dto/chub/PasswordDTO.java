package com.panera.cmt.dto.chub;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.panera.cmt.entity.ResponseHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PasswordDTO {

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String password;

    public static PasswordDTO fromEntity(ResponseHolder<String> entity) {
        if (entity == null) {
            return null;
        }

        PasswordDTO dto = new PasswordDTO();
        dto.setPassword(entity.getEntity());

        return dto;
    }
}
