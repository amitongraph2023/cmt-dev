package com.panera.cmt.dto.proxy.chub;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GeneralPreference {

    private List<PersonGeneralPreference> foodPreferences;
    private PersonGeneralPreference gatherPreference;

    // Getters
    public List<PersonGeneralPreference> getFoodPreferences() {
        return (foodPreferences != null) ? foodPreferences : new ArrayList<>();
    }
}
