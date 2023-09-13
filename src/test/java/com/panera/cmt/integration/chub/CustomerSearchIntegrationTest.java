package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.CustomerSearchType;
import com.panera.cmt.service.chub.ICustomerSearchService;
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
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.randomEmailAddress;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@Transactional
public class CustomerSearchIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerSearchService customerSearchService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "paytronixApigeeRestTemplate")
    private RestTemplate paytronixRestTemplate;

    @MockBean
    private SSOController ssoController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ReflectionTestUtils.setField(customerSearchService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void searchCustomers_ByEmail_CustomerExists() throws Exception {
        CustomerSearchType type = CustomerSearchType.email;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);
        Map<String, Object> customer = new ChubFactoryUtil().searchResult(emailAddress, phoneNumber).build();
        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(customer)))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type, value)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(customer.get("username"))))
                .andExpect(jsonPath("$[0].firstName", is(customer.get("firstName"))))
                .andExpect(jsonPath("$[0].lastName", is(customer.get("lastName"))))
                .andExpect(jsonPath("$[0].defaultEmail", is(emailAddress)))
                .andExpect(jsonPath("$[0].defaultPhone", is(phoneNumber)));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));
    }
    @Test
    public void searchCustomers_ByEmail_CustomerDoesNotExist() throws Exception {
        CustomerSearchType type = CustomerSearchType.email;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);
        Map<String, Object> customer = new ChubFactoryUtil().searchResult(emailAddress, phoneNumber).build();
        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emptyList()))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type, value)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));
    }
    @Test
    public void searchCustomers_ByUsername_CustomerExists() throws Exception {
        CustomerSearchType type = CustomerSearchType.username;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);
        Map<String, Object> customer = new ChubFactoryUtil().searchResult(emailAddress, phoneNumber).build();
        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(customer)))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type, value)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(customerDetails.get("username"))))
                .andExpect(jsonPath("$[0].firstName", is(customerDetails.get("firstName"))))
                .andExpect(jsonPath("$[0].lastName", is(customerDetails.get("lastName"))))
                .andExpect(jsonPath("$[0].defaultEmail", is(emailAddress)))
                .andExpect(jsonPath("$[0].defaultPhone", is(phoneNumber)));

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));
    }
    @Test
    public void searchCustomers_ByUsername_CustomerDoesNotExist() throws Exception {
        CustomerSearchType type = CustomerSearchType.username;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);
        Map<String, Object> customer = new ChubFactoryUtil().searchResult(emailAddress, phoneNumber).build();
        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(customer)))));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("")));

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        mockMvc.perform(get(String.format("/api/v1/customer/search?type=%s&value=%s", type, value)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));
    }
}
