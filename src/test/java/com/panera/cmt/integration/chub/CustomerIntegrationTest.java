package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.CustomerDTO;
import com.panera.cmt.dto.proxy.chub.TaxExemption;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.enums.UpdateAccountStatusAction;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.chub.ICustomerService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_builders.CustomerDTOBuilder;
import com.panera.cmt.test_builders.TaxExemptionBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
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
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
public class CustomerIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerService customerService;

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
        ReflectionTestUtils.setField(customerService, "baseUrl", "http://localhost:" + wireMockRule.port());
        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getCustomer_CustomerExists() throws Exception {
        Long id = random.nextLong();
        Map<String, Object> customer = new ChubFactoryUtil().extended(false, 0, 0).build(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.customerId", isIntLong((Long)customer.get("customerId"))))
                .andExpect(jsonPath("$.username", is(customer.get("username"))))
                .andExpect(jsonPath("$.firstName", is(customer.get("firstName"))))
                .andExpect(jsonPath("$.lastName", is(customer.get("lastName"))))
                .andExpect(jsonPath("$.isSmsGlobalOpt", is(customer.get("isSmsGlobalOpt"))))
                .andExpect(jsonPath("$.isEmailGlobalOpt", is(customer.get("isEmailGlobalOpt"))))
                .andExpect(jsonPath("$.isMobilePushOpt", is(customer.get("isMobilePushOpt"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
    }
    @Test
    public void getCustomer_CustomerDoesNotExist() throws Exception {
        Long id = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d", id)))
                .andExpect(status().isNotFound());

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
    }

    @Test
    public void getCustomerDetails_CustomerExists() throws Exception {
        Long id = random.nextLong();
        String facebookId = randomAlphanumeric(15);
        String googleId = randomAlphanumeric(15);
        TaxExemption taxExemption = new TaxExemptionBuilder().build();
        Map<String, Object> customer = new ChubFactoryUtil().extended(true, 0, 0).withBirthday().withSocialIntegration(facebookId, googleId).withTaxExemption(taxExemption).build(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/details", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.customerId", isIntLong((Long)customer.get("customerId"))))
                .andExpect(jsonPath("$.username", is(customer.get("username"))))
                .andExpect(jsonPath("$.firstName", is(customer.get("firstName"))))
                .andExpect(jsonPath("$.lastName", is(customer.get("lastName"))))
                .andExpect(jsonPath("$.isSmsGlobalOpt", is(customer.get("isSmsGlobalOpt"))))
                .andExpect(jsonPath("$.isEmailGlobalOpt", is(customer.get("isEmailGlobalOpt"))))
                .andExpect(jsonPath("$.isMobilePushOpt", is(customer.get("isMobilePushOpt"))))
                .andExpect(jsonPath("$.loyaltyCardNumber", is(((Map)customer.get("loyalty")).get("cardNumber"))))
                .andExpect(jsonPath("$.dob", is(((Map)customer.get("birthDate")).get("birthDay") + "/" + ((Map)customer.get("birthDate")).get("birthMonth"))))
                .andExpect(jsonPath("$.socialIntegration.facebookIntegration.facebookId", is(facebookId)))
                .andExpect(jsonPath("$.socialIntegration.googleIntegration.googleId", is(googleId)))
                .andExpect(jsonPath("$.taxExemptions", hasSize(1)))
                .andExpect(jsonPath("$.taxExemptions[0].company", is(taxExemption.getCompany())))
                .andExpect(jsonPath("$.taxExemptions[0].state", is(taxExemption.getState())))
                .andExpect(jsonPath("$.taxExemptions[0].country", is(taxExemption.getCountry())));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));
    }
    @Test
    public void getCustomerDetails_CustomerDoesNotExist() throws Exception {
        Long id = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/details", id)))
                .andExpect(status().isNotFound());

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));
    }

    @Test
    public void updateCustomer_ResponseStatusIs200_Expect204() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();
        Map<String, Object> customerMap = new ChubFactoryUtil().extended(false, 0, 0).build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerMap))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
    }
    @Test
    public void updateCustomer_ResponseStatusIs404_Expect404() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
    }
    @Test
    public void updateCustomer_ResponseStatusIs406_Expect406() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
    }
    @Test
    public void updateCustomer_ResponseStatusIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
    }
}
