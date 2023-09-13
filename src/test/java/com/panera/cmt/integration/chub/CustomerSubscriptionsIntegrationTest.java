package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.chub.CustomerSubscriptionsDTO;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.service.chub.ICustomerSubscriptionService;
import com.panera.cmt.test_builders.CustomerSubscriptionsDTOBuilder;
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

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
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
@SuppressWarnings("unchecked")
@Transactional
public class CustomerSubscriptionsIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerSubscriptionService customerSubscriptionService;

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
        ReflectionTestUtils.setField(customerSubscriptionService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void getSubscriptions_SubscriptionsAreFound_Expect200() throws Exception {
        Long customerId = random.nextLong();

        Map<String, Object> subscriptions = new ChubFactoryUtil().buildAsSubscriptions();
        Map<String, Object> subscription = ((List<Map<String, Object>>)subscriptions.get("subscriptions")).get(0);
        Map<String, Object> suppressor = ((List<Map<String, Object>>)subscriptions.get("suppressors")).get(0);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(subscriptions))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/subscriptions", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.subscriptions", hasSize(1)))
                .andExpect(jsonPath("$.subscriptions[0].subscriptionCode", is(subscription.get("subscriptionCode"))))
                .andExpect(jsonPath("$.subscriptions[0].displayName", is(subscription.get("displayName"))))
                .andExpect(jsonPath("$.subscriptions[0].isSubscribed", is(subscription.get("isSubscribed"))))
                .andExpect(jsonPath("$.suppressors", hasSize(1)))
                .andExpect(jsonPath("$.suppressors[0].suppressionCode", is(suppressor.get("suppressionCode"))))
                .andExpect(jsonPath("$.suppressors[0].displayName", is(suppressor.get("displayName"))))
                .andExpect(jsonPath("$.suppressors[0].isSuppressed", is(suppressor.get("isSuppressed"))));


        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
    }
    @Test
    public void getSubscriptions_SubscriptionsAreNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/subscriptions", customerId)))
                .andExpect(status().isNotFound());


        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
    }

    @Test
    public void updateSubscriptions_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        WireMock.verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
    }
    @Test
    public void updateSubscriptions_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
    }
    @Test
    public void updateSubscriptions_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(406)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        WireMock.verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
    }
    @Test
    public void updateSubscriptions_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
    }
}
