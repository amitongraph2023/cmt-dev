package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.PhoneDTO;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.service.chub.ICustomerPhoneService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.PhoneDTOBuilder;
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

import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class CustomerPhoneIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerPhoneService customerPhoneService;

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
        ReflectionTestUtils.setField(customerPhoneService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void addPhone_SuccessfullyCreated_Expect201() throws Exception {
        Long customerId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
    }
    @Test
    public void addPhone_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
    }
    @Test
    public void addPhone_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
    }
    @Test
    public void addPhone_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
    }

    @Test
    public void deletePhone_SuccessfullyDeleted_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNoContent());

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void deletePhone_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNotFound());

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void deletePhone_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void deletePhone_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isInternalServerError());

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }

    @Test
    public void getPhone_PhoneIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phone))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong((Long)phone.get("id"))))
                .andExpect(jsonPath("$.phoneNumber", is(phone.get("phoneNumber"))))
                .andExpect(jsonPath("$.phoneType", is(phone.get("phoneType"))))
                .andExpect(jsonPath("$.countryCode", is(phone.get("countryCode"))))
                .andExpect(jsonPath("$.extension", is(phone.get("extension"))))
                .andExpect(jsonPath("$.name", is(phone.get("name"))))
                .andExpect(jsonPath("$.isCallOpt", is(phone.get("isCallOpt"))))
                .andExpect(jsonPath("$.isDefault", is(phone.get("isDefault"))))
                .andExpect(jsonPath("$.isValid", is(phone.get("isValid"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void getPhone_PhoneIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId)))
                .andExpect(status().isNotFound());

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }

    @Test
    public void getPhones_PhoneIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(phone)))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIntLong((Long)phone.get("id"))))
                .andExpect(jsonPath("$[0].phoneNumber", is(phone.get("phoneNumber"))))
                .andExpect(jsonPath("$[0].phoneType", is(phone.get("phoneType"))))
                .andExpect(jsonPath("$[0].countryCode", is(phone.get("countryCode"))))
                .andExpect(jsonPath("$[0].extension", is(phone.get("extension"))))
                .andExpect(jsonPath("$[0].name", is(phone.get("name"))))
                .andExpect(jsonPath("$[0].isCallOpt", is(phone.get("isCallOpt"))))
                .andExpect(jsonPath("$[0].isDefault", is(phone.get("isDefault"))))
                .andExpect(jsonPath("$[0].isValid", is(phone.get("isValid"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
    }
    @Test
    public void getPhones_PhoneIsNotFound_Expect200WithEmptyList() throws Exception {
        Long customerId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/phone", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
    }

    @Test
    public void setDefault_SuccessfullySetToDefault_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isNoContent());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
    }
    @Test
    public void setDefault_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isNotFound());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
    }
    @Test
    public void setDefault_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
    }
    @Test
    public void setDefault_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/phone/%d/default", customerId, phoneId)))
                .andExpect(status().isInternalServerError());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
    }

    @Test
    public void updatePhone_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void updatePhone_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void updatePhone_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
    @Test
    public void updatePhone_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        PhoneDTO dto = new PhoneDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/phone/%d", customerId, phoneId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
    }
}
