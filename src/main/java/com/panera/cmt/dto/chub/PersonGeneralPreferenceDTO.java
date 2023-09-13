package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;
import lombok.Data;

@Data
public class PersonGeneralPreferenceDTO {

    private Integer code;
    private String displayName;

    public static PersonGeneralPreferenceDTO fromEntity(PersonGeneralPreference entity) {
        if (entity == null) {
            return null;
        }

        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTO();
        dto.setCode(entity.getCode());
        dto.setDisplayName(entity.getDisplayName());

        return dto;
    }

    public PersonGeneralPreference toEntity() {
        PersonGeneralPreference entity = new PersonGeneralPreference();
        entity.setCode(code);
        entity.setDisplayName(displayName);

        return entity;
    }
}
