package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.GeneralPreferenceDTO;
import com.panera.cmt.dto.chub.PersonGeneralPreferenceDTO;

import java.util.List;

import static java.util.Collections.singletonList;

public class GeneralPreferenceDTOBuilder extends BaseObjectBuilder<GeneralPreferenceDTO> {

    private List<PersonGeneralPreferenceDTO> foodPreferences = singletonList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());
    private PersonGeneralPreferenceDTO gatherPreference = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();

    @Override
    GeneralPreferenceDTO getTestClass() {
        return new GeneralPreferenceDTO();
    }
}
