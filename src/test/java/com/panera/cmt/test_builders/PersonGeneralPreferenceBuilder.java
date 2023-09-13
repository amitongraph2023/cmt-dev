package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;

import static com.panera.cmt.test_util.SharedTestUtil.randomRange;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class PersonGeneralPreferenceBuilder extends BaseObjectBuilder<PersonGeneralPreference> {

    private Integer code = randomRange(1, 14);
    private String displayName = randomAlphabetic(10);

    public PersonGeneralPreferenceBuilder asFoodPreference() {
        code = 1;
        displayName = "Dairy Free";
        return this;
    }

    public PersonGeneralPreferenceBuilder asGatherPreference() {
        code = 14;
        displayName = "Other";
        return this;
    }

    public PersonGeneralPreferenceBuilder withCode(Integer code) {
        this.code = code;
        return this;
    }

    public PersonGeneralPreferenceBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    PersonGeneralPreference getTestClass() {
        return new PersonGeneralPreference();
    }
}
