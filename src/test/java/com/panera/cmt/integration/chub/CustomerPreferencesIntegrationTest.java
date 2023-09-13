package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.GeneralPreferenceDTO;
import com.panera.cmt.dto.chub.PersonGeneralPreferenceDTO;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.service.chub.ICustomerPreferencesService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.GeneralPreferenceDTOBuilder;
import com.panera.cmt.test_builders.PersonGeneralPreferenceDTOBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@Transactional
public class CustomerPreferencesIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerPreferencesService customerPreferencesService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private Random random = new Random();

    @MockBean(name = "paytronixApigeeRestTemplate")
    private RestTemplate paytronixRestTemplate;

    @MockBean
    private SSOController ssoController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ReflectionTestUtils.setField(customerPreferencesService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    @Ignore //  todp fix when adding default payment type
    public void getUserPreferences_CustomerPreferencesIsFound_Expect200() throws Exception {
        Long id = random.nextLong();
        Map<String, Object> preferences = new ChubFactoryUtil().buildAsUserPreferences();
        Map<String, Object> foodPreference = ((List<Map<String, Object>>)preferences.get("foodPreferences")).get(0);
        Map<String, Object> gatherPreference = (Map) preferences.get("gatherPreference");


        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(preferences))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/userpreferences", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.foodPreferences", hasSize(1)))
                .andExpect(jsonPath("$.foodPreferences[0].code", is(foodPreference.get("code"))))
                .andExpect(jsonPath("$.foodPreferences[0].displayName", is(foodPreference.get("displayName"))))
                .andExpect(jsonPath("$.gatherPreference.code", is(gatherPreference.get("code"))))
                .andExpect(jsonPath("$.gatherPreference.displayName", is(gatherPreference.get("displayName"))))
        ;

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
    }
    @Test
    @Ignore // Verify that this is still applicable in the code when updating for default subscription payment type
    public void getUserPreferences_CustomerPreferencesIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/userpreferences", id)))
                .andExpect(status().isNotFound());

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
    }

    @Test
    public void updateFoodPreferences_SuccessfulUpdate_Expect204() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }
    @Test
    public void updateFoodPreferences_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }
    @Test
    public void updateFoodPreferences_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().build());
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }
    @Test
    public void updateFoodPreferences_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        List<PersonGeneralPreferenceDTO> dto = asList(new PersonGeneralPreferenceDTOBuilder().asFoodPreference().build());

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/dietary", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }

    @Test
    public void updateGatherPreference_SuccessfulUpdate_Expect204() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }
    @Test
    public void updateGatherPreference_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }
    @Test
    public void updateGatherPreference_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }
    @Test
    public void updateGatherPreference_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        PersonGeneralPreferenceDTO dto = new PersonGeneralPreferenceDTOBuilder().asGatherPreference().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences/gather", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BY_TYPE))));
    }

    @Test
    public void updateUserPreferences_SuccessfulUpdate_Expect204() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
    }
    @Test
    public void updateUserPreferences_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
    }
    @Test
    public void updateUserPreferences_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
    }
    @Test
    public void updateUserPreferences_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        GeneralPreferenceDTO dto = new GeneralPreferenceDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/userpreferences", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USER_PREFERENCES_BASE))));
    }
}