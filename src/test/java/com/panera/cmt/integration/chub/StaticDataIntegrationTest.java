package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.service.chub.IStaticDataService;
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

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
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
public class StaticDataIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private IStaticDataService staticDataService;

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
        ReflectionTestUtils.setField(staticDataService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void searchCustomers_DataIsFound_Expect200() throws Exception {
        String type = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();
        Object[] objects = {value};

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(objects))));

        mockMvc.perform(get(String.format("/api/v1/static/chub/%s", type)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is(value)));

        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));
    }
    @Test
    public void searchCustomers_DataIsNotFound_Expect404() throws Exception {
        String type = UUID.randomUUID().toString();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/static/chub/%s", type)))
                .andExpect(status().isNotFound());

        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));
    }
    @Test
    public void searchCustomers_ResponseIs500_Expect404() throws Exception {
        String type = UUID.randomUUID().toString();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(get(String.format("/api/v1/static/chub/%s", type)))
                .andExpect(status().isNotFound());

        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.STATIC_DATA_BY_TYPE))));
    }
}
