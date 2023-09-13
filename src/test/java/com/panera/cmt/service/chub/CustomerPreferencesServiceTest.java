package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.GeneralPreference;
import com.panera.cmt.dto.proxy.chub.PersonGeneralPreference;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_builders.GeneralPreferenceBuilder;
import com.panera.cmt.test_builders.PersonGeneralPreferenceBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PREFERENCES;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@SuppressWarnings({"Duplicates", "unchecked"})
public class CustomerPreferencesServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @Mock
    private IAppConfigLocalService appConfigService;

    @InjectMocks
    private CustomerPreferencesService classUnderTest;

    private String auditSubject = AUDIT_SUBJECT_PREFERENCES;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getPreferences_PreferencesAreFound_ExpectOptionalOfGeneralPreferences() {
        Long customerId = random.nextLong();
        Map<String, Object> preferences = new ChubFactoryUtil().buildAsUserPreferences();
        Map<String, Object> foodPreference = ((List<Map<String, Object>>)preferences.get("foodPreferences")).get(0);
        Map<String, Object> gatherPreference = (Map) preferences.get("gatherPreference");

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(preferences))));

        Optional<GeneralPreference> result = classUnderTest.getPreferences(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));

        assertTrue(result.isPresent());
        assertNotNull(foodPreference);
        assertNotNull(result.get().getFoodPreferences());
        assertEquals(1, ((List)preferences.get("foodPreferences")).size());
        assertNotNull(result.get().getFoodPreferences().get(0));
        assertEquals(foodPreference.get("code"), result.get().getFoodPreferences().get(0).getCode());
        assertEquals(foodPreference.get("displayName"), result.get().getFoodPreferences().get(0).getDisplayName());
        assertNotNull(gatherPreference);
        assertNotNull(result.get().getGatherPreference());
        assertEquals(gatherPreference.get("code"), result.get().getGatherPreference().getCode());
        assertEquals(gatherPreference.get("displayName"), result.get().getGatherPreference().getDisplayName());
    }
    @Test
    public void getPreferences_PreferencesAreNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(404)));

        Optional<GeneralPreference> result = classUnderTest.getPreferences(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPreferences_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(500)));

        Optional<GeneralPreference> result = classUnderTest.getPreferences(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPreferences_CustomerIdIsNull_ExpectEmptyOptional() {
        Map<String, Object> preferences = new ChubFactoryUtil().buildAsUserPreferences();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(preferences))));

        Optional<GeneralPreference> result = classUnderTest.getPreferences(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));

        assertFalse(result.isPresent());
    }

    @Test
    public void updateFoodPreferences_SuccessfulUpdate_ExpectOptionalOfResponseHolderWith204Status() {
        Long customerId = random.nextLong();
        List<PersonGeneralPreference> preferences = asList(new PersonGeneralPreferenceBuilder().asFoodPreference().build());
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateFoodPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateFoodPreferences_PreferencesIsNull_ExpectOptionalOfResponseHolderWith204Status() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateFoodPreferences(customerId, null);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateFoodPreferences_CustomerIsNotFound_ExpectOptionalOfResponseHolderWith404Status() {
        Long customerId = random.nextLong();
        List<PersonGeneralPreference> preferences = asList(new PersonGeneralPreferenceBuilder().asFoodPreference().build());
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateFoodPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateFoodPreferences_ValidationError_ExpectOptionalOfResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        List<PersonGeneralPreference> preferences = asList(new PersonGeneralPreferenceBuilder().asFoodPreference().build());
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateFoodPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateFoodPreferences_CustomerHubResponseIs500_ExpectOptionalOfResponseHolderWith500Status() {
        Long customerId = random.nextLong();
        List<PersonGeneralPreference> preferences = asList(new PersonGeneralPreferenceBuilder().asFoodPreference().build());
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateFoodPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateFoodPreferences_CustomerIdIsNull_ExpectEmptyOptional() {
        List<PersonGeneralPreference> preferences = asList(new PersonGeneralPreferenceBuilder().asFoodPreference().build());
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateFoodPreferences(null, preferences);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void updateGatherPreference_SuccessfulUpdate_ExpectOptionalOfResponseHolderWith204Status() {
        Long customerId = random.nextLong();
        PersonGeneralPreference preference = new PersonGeneralPreferenceBuilder().asGatherPreference().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateGatherPreference(customerId, preference);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateGatherPreference_CustomerIsNotFound_ExpectOptionalOfResponseHolderWith404Status() {
        Long customerId = random.nextLong();
        PersonGeneralPreference preference = new PersonGeneralPreferenceBuilder().asGatherPreference().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateGatherPreference(customerId, preference);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateGatherPreference_ValidationError_ExpectOptionalOfResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        PersonGeneralPreference preference = new PersonGeneralPreferenceBuilder().asGatherPreference().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateGatherPreference(customerId, preference);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateGatherPreference_CustomerHubResponseIs500_ExpectOptionalOfResponseHolderWith500Status() {
        Long customerId = random.nextLong();
        PersonGeneralPreference preference = new PersonGeneralPreferenceBuilder().asGatherPreference().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateGatherPreference(customerId, preference);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateGatherPreference_CustomerIdIsNull_ExpectEmptyOptional() {
        PersonGeneralPreference preference = new PersonGeneralPreferenceBuilder().asGatherPreference().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateGatherPreference(null, preference);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateGatherPreference_PreferenceIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PersonGeneralPreference>> result = classUnderTest.updateGatherPreference(customerId, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void updateUserPreference_SuccessfulUpdate_ExpectOptionalOfResponseHolderWith204Status() {
        Long customerId = random.nextLong();
        GeneralPreference preferences = new GeneralPreferenceBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateUserPreference_FoodPreferencesIsNull_SuccessfulUpdate_ExpectOptionalOfResponseHolderWith204Status() {
        Long customerId = random.nextLong();
        GeneralPreference preferences = new GeneralPreferenceBuilder().withFoodPreferences((List) null).build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateUserPreference_CustomerIsNotFound_ExpectOptionalOfResponseHolderWith404Status() {
        Long customerId = random.nextLong();
        GeneralPreference preferences = new GeneralPreferenceBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateUserPreference_ValidationError_ExpectOptionalOfResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        GeneralPreference preferences = new GeneralPreferenceBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateUserPreference_CustomerHubResponseIs500_ExpectOptionalOfResponseHolderWith500Status() {
        Long customerId = random.nextLong();
        GeneralPreference preferences = new GeneralPreferenceBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, preferences);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateUserPreference_CustomerIdIsNull_ExpectEmptyOptional() {
        GeneralPreference preferences = new GeneralPreferenceBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(null, preferences);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateUserPreference_PreferencesIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateUserPreference_GatherPreferenceIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        GeneralPreference preferences = new GeneralPreferenceBuilder().withGatherPreference(null).build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GeneralPreference>> result = classUnderTest.updateUserPreferences(customerId, preferences);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
}