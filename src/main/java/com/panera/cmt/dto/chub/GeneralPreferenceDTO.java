package com.panera.cmt.dto.chub;

import com.panera.cmt.dto.proxy.chub.GeneralPreference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class GeneralPreferenceDTO {

    private List<PersonGeneralPreferenceDTO> foodPreferences;
    private PersonGeneralPreferenceDTO gatherPreference;

    // Getters
    public List<PersonGeneralPreferenceDTO> getFoodPreferences() {
        return (foodPreferences != null) ? foodPreferences : new ArrayList<>();
    }

    public static GeneralPreferenceDTO fromEntity(GeneralPreference entity) {
        if (entity == null) {
            return null;
        }

        GeneralPreferenceDTO dto = new GeneralPreferenceDTO();
        dto.setFoodPreferences(entity.getFoodPreferences().stream()
                .map(PersonGeneralPreferenceDTO::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        dto.setGatherPreference(PersonGeneralPreferenceDTO.fromEntity(entity.getGatherPreference()));

        return dto;
    }

    public GeneralPreference toEntity() {
        GeneralPreference entity = new GeneralPreference();
        entity.setFoodPreferences(getFoodPreferences().stream()
                .map(PersonGeneralPreferenceDTO::toEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        entity.setGatherPreference(gatherPreference.toEntity());

        return entity;
    }
}
