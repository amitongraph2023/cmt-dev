package com.panera.cmt.dto.app_config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.serializer.DateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class AppConfigDTO {

    @ApiModelProperty(value = "id", readOnly = true, position = 1)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "error.code.required")
    @Size(max = 50, message = "error.code.length")
    private String code;

    @NotNull(message = "error.value.required")
    @Size(max = 1000, message = "error.value.length")
    private String value;

    @JsonSerialize(using = DateSerializer.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastUpdatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastUpdatedBy;

    public static AppConfigDTO fromEntity(AppConfig entity) {
        if (entity == null) {
            return null;
        }

        AppConfigDTO dto = new AppConfigDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setValue(entity.getValue());
        dto.setLastUpdatedAt(entity.getLastUpdatedAt());
        dto.setLastUpdatedBy(entity.getLastUpdatedBy());

        return dto;
    }

    public AppConfig toEntity() {
        AppConfig entity = new AppConfig();
        entity.setCode(code);
        entity.setValue(value);
        return entity;
    }
}
