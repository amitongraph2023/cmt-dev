package com.panera.cmt.integration;

import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.repository.IAppConfigRepository;
import com.panera.cmt.test_builders.AppConfigBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static com.panera.cmt.config.Constants.APP_CONFIG_UI_ROUTE_WHITELIST;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@Transactional
public class UIConfigIntegrationTest {

    @Autowired
    private IAppConfigRepository appConfigRepository;

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
    }

    @Test
    @Ignore // todo whitelists as is was removed, so revisit to reflect replacement of whitelists
    public void getRouteWhiteLists_WhitelistIsFound() throws Exception {
        AppConfig appConfig = this.appConfigRepository.save(new AppConfigBuilder().withCode(APP_CONFIG_UI_ROUTE_WHITELIST + ".test").build(true));

        mockMvc.perform(get("/api/v1/ui-config/route-whitelist"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.['" + appConfig.getCode() + "']").isNotEmpty())
                .andExpect(jsonPath("$.['" + appConfig.getCode() + "']", hasSize(1)))
                .andExpect(jsonPath("$.['" + appConfig.getCode() + "'][0]", is(appConfig.getValue())));
    }
}