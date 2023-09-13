package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.paytronix.PaytronixEsbController;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.enums.UpdateAccountStatusAction;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.chub.ICustomerService;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@Transactional
public class ManageAccountIntegrationTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private Random random = new Random();

    @MockBean
    private SSOController ssoController;

    @MockBean(name = "paytronixApigeeRestTemplate")
    private RestTemplate paytronixRestTemplate;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ReflectionTestUtils.setField(customerService, "baseUrl", "http://localhost:" + wireMockRule.port());
        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void updateAccountStatus_ReinstateAccount() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.REINSTATE;

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.name())))
                .andExpect(status().isOk());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT))));
    }
    @Test
    public void updateAccountStatus_SuspendAccount() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.SUSPEND;

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.name())))
                .andExpect(status().isOk());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT))));
    }
    @Test
    public void updateAccountStatus_TerminateAccount() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.TERMINATE;

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .withRequestBody(matchingJsonPath("$.reason", containing("FRAUD")))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.name())))
                .andExpect(status().isOk());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .withRequestBody(matchingJsonPath("$.reason", containing("FRAUD"))));
    }
}
