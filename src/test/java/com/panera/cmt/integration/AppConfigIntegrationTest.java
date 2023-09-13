package com.panera.cmt.integration;

import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.app_config.AppConfigDTO;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.repository.IAppConfigRepository;
import com.panera.cmt.test_builders.AppConfigBuilder;
import com.panera.cmt.test_builders.AppConfigDTOBuilder;
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

import java.util.Optional;

import static com.panera.cmt.test_util.SharedTestUtil.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@SuppressWarnings("Duplicates")
@Transactional
public class AppConfigIntegrationTest {

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
    public void addAppConfig() throws Exception {
        AppConfigDTO dto = new AppConfigDTOBuilder().build();

        mockMvc.perform(post("/api/v1/app-config")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        AppConfig appConfig = appConfigRepository.getByCode(dto.getCode()).orElse(null);

        assertNotNull(appConfig);
        assertEquals(dto.getCode(), appConfig.getCode());
        assertEquals(dto.getValue(), appConfig.getValue());
    }

    @Test
    public void deleteAppConfigByCode() throws Exception {
        AppConfig appConfig = appConfigRepository.save(new AppConfigBuilder().build());

        mockMvc.perform(delete(String.format("/api/v1/app-config/%d", appConfig.getId())))
                .andExpect(status().isNoContent());

        assertFalse(appConfigRepository.findById(appConfig.getId()).isPresent());
    }

    @Test
    public void getAllAppConfigsPaged() throws Exception {
        AppConfig appConfig = this.appConfigRepository.save(new AppConfigBuilder().build(true));

        mockMvc.perform(get("/api/v1/app-config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.content[0].id", isIntLong(appConfig.getId())))
                .andExpect(jsonPath("$.content[0].code", is(appConfig.getCode())))
                .andExpect(jsonPath("$.content[0].value", is(appConfig.getValue())))
                .andExpect(jsonPath("$.content[0].lastUpdatedAt", isDate(appConfig.getLastUpdatedAt())))
                .andExpect(jsonPath("$.content[0].lastUpdatedBy", is(appConfig.getLastUpdatedBy())));
    }

    @Test
    public void updateAppConfig() throws Exception {
        AppConfig appConfig = appConfigRepository.save(new AppConfigBuilder().build());
        AppConfigDTO dto = new AppConfigDTOBuilder().build();

        mockMvc.perform(put(String.format("/api/v1/app-config/%d", appConfig.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        Optional<AppConfig> result = appConfigRepository.findById(appConfig.getId());

        assertTrue(result.isPresent());
        assertEquals(appConfig.getId(), result.get().getId());
        assertEquals(dto.getCode(), result.get().getCode());
        assertEquals(dto.getValue(), result.get().getValue());
    }
}