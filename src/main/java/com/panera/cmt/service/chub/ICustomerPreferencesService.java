package com.panera.cmt.service.chub;

import com.panera.cmt.dto.proxy.chub.GeneralPreference;
import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;
import com.panera.cmt.entity.ResponseHolder;

import java.util.List;
import java.util.Optional;

public interface ICustomerPreferencesService {

    Optional<GeneralPreference> getPreferences(Long customerId);

    Optional<ResponseHolder<PersonGeneralPreference>> updateFoodPreferences(Long customerId, List<PersonGeneralPreference> preferences);

    Optional<ResponseHolder<PersonGeneralPreference>> updateGatherPreference(Long customerId, PersonGeneralPreference preference);

    Optional<ResponseHolder<GeneralPreference>> updateUserPreferences(Long customerId, GeneralPreference userPreferences);
}
