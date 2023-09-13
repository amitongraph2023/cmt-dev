package com.panera.cmt.service.chub;

import com.panera.cmt.dto.chub.GeneralPreferenceDTO;
import com.panera.cmt.dto.chub.PersonGeneralPreferenceDTO;
import com.panera.cmt.dto.proxy.chub.GeneralPreference;
import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PREFERENCES;
import static com.panera.cmt.util.SharedUtils.isNull;

@Service
@Slf4j
public class CustomerPreferencesService extends BaseCustomerHubService implements ICustomerPreferencesService {

    @Override
    public Optional<GeneralPreference> getPreferences(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "getPreferences", String.format("Getting user preferences for customerId=%d", customerId));

        return Optional.ofNullable(doGet(GeneralPreference.class, stopWatch, ChubEndpoints.USER_PREFERENCES_BASE, customerId));
    }

    @Override
    public Optional<ResponseHolder<PersonGeneralPreference>> updateFoodPreferences(Long customerId, List<PersonGeneralPreference> preferences) {
        if (customerId == null) {
            return Optional.empty();
        }

        if (preferences == null) {
            preferences = new ArrayList<>();
        }

        StopWatch stopWatch = new StopWatch(log, "updateFoodPreferences", String.format("Updating food preferences for customerId=%d", customerId));

        List<PersonGeneralPreferenceDTO> dtos = preferences.stream()
                .map(PersonGeneralPreferenceDTO::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Optional.ofNullable(doPut(PersonGeneralPreference.class, stopWatch, createAudit(ActionType.UPDATE, customerId, preferences), dtos, ChubEndpoints.USER_PREFERENCES_BY_TYPE, customerId, "food"));
    }

    @Override
    public Optional<ResponseHolder<PersonGeneralPreference>> updateGatherPreference(Long customerId, PersonGeneralPreference preference) {
        if (isNull(customerId, preference)) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateGatherPreference", String.format("Updating gather preferences for customerId=%d", customerId));

        return Optional.ofNullable(doPut(PersonGeneralPreference.class, stopWatch, createAudit(ActionType.UPDATE, customerId, preference), PersonGeneralPreferenceDTO.fromEntity(preference), ChubEndpoints.USER_PREFERENCES_BY_TYPE, customerId, "gather"));
    }

    @Override
    public Optional<ResponseHolder<GeneralPreference>> updateUserPreferences(Long customerId, GeneralPreference userPreferences) {
        if (isNull(customerId, userPreferences) || userPreferences.getGatherPreference() == null) {
            return Optional.empty();
        }

        StopWatch stopWatch = new StopWatch(log, "updateUserPreferences", String.format("Updating user preferences for customerId=%d", customerId));

        return Optional.ofNullable(doPut(GeneralPreference.class, stopWatch, createAudit(ActionType.UPDATE, customerId, userPreferences), GeneralPreferenceDTO.fromEntity(userPreferences), ChubEndpoints.USER_PREFERENCES_BASE, customerId));
    }

    @Override
    protected String getSubjectName() {
        return AUDIT_SUBJECT_PREFERENCES;
    }
}
