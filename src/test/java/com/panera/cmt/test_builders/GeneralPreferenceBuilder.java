package com.panera.cmt.test_builders;

import com.panera.cmt.dto.proxy.chub.GeneralPreference;
import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class GeneralPreferenceBuilder extends BaseObjectBuilder<GeneralPreference> {

    private List<PersonGeneralPreference> foodPreferences = singletonList(new PersonGeneralPreferenceBuilder().asFoodPreference().build());
    private PersonGeneralPreference gatherPreference = new PersonGeneralPreferenceBuilder().asGatherPreference().build();

    public GeneralPreferenceBuilder withFoodPreferences(List<PersonGeneralPreference> foodPreferences) {
        this.foodPreferences = foodPreferences;
        return this;
    }
    public GeneralPreferenceBuilder withFoodPreferences(PersonGeneralPreference foodPreference) {
        if (foodPreference == null) {
            this.foodPreferences = emptyList();
        } else {
            this.foodPreferences = singletonList(foodPreference);
        }
        return this;
    }

    public GeneralPreferenceBuilder withGatherPreference(PersonGeneralPreference gatherPreference) {
        this.gatherPreference = gatherPreference;
        return this;
    }


    @Override
    GeneralPreference getTestClass() {
        return new GeneralPreference();
    }
}
