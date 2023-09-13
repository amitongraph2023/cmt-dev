package com.panera.cmt.test_builders;

import com.panera.cmt.dto.chub.PersonGeneralPreferenceDTO;

import static com.panera.cmt.test_util.SharedTestUtil.randomRange;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class PersonGeneralPreferenceDTOBuilder extends BaseObjectBuilder<PersonGeneralPreferenceDTO> {

    private Integer code = randomRange(1, 14);
    private String displayName = randomAlphabetic(10);

    public PersonGeneralPreferenceDTOBuilder asFoodPreference() {
        code = 1;
        displayName = "Dairy Free";
        return this;
    }

    public PersonGeneralPreferenceDTOBuilder asGatherPreference() {
        code = 14;
        displayName = "Other";
        return this;
    }

    public PersonGeneralPreferenceDTOBuilder withCode(Integer code) {
        this.code = code;
        return this;
    }

    public PersonGeneralPreferenceDTOBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    PersonGeneralPreferenceDTO getTestClass() {
        return new PersonGeneralPreferenceDTO();
    }
}
