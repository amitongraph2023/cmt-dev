package com.panera.cmt.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mongodb.MongoClient;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.AuthRequestDTO;
import com.panera.cmt.dto.AuthenticationTokenDTO;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuthenticatedUserRepository;
import com.panera.cmt.repository.IAppConfigRepository;
import com.panera.cmt.service.IAuthenticationService;
import com.panera.cmt.service.app_config.IAuthGroupsService;
import com.panera.cmt.test_builders.AppConfigBuilder;
import com.panera.cmt.test_builders.AuthRequestDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_builders.Iso3ResponseBuilder;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.APP_CONFIG_AUTH_GROUP;
import static com.panera.cmt.config.Constants.SSO_COOKIE_NAME;
import static com.panera.cmt.test_util.SharedTestUtil.*;
import static com.panera.cmt.util.SharedUtils.createCookie;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@SuppressWarnings("Duplicates")
@Transactional
public class AuthenticationIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private IAppConfigRepository appConfigRepository;

    @Autowired
    private IAuthenticatedUserRepository authenticatedUserRepository;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IAuthGroupsService authGroupsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private AppConfig appConfig;
    private AuthenticatedUser authUser = new AuthenticatedUserBuilder().build();
    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;

    @MockBean(name = "paytronixApigeeRestTemplate")
    private RestTemplate paytronixRestTemplate;

    @MockBean
    private SSOController ssoController;

    @Before
    public void setUp() throws Exception {
        if (appConfig == null) {
            AppConfig appConfig = new AppConfigBuilder().withCode(APP_CONFIG_AUTH_GROUP + ".admin").withValue("WTM_BAKERS").build();
            appConfigRepository.save(appConfig);
        }

        String ip = "localhost";
        int port = 27017;

        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(new MongoClient(ip, port), "test");

        AuthenticatedUserManager.setAuthenticatedUser(authUser);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ReflectionTestUtils.setField(authenticationService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @After
    public void tearDown() {
        mongodExecutable.stop();
    }

    @Test
    public void getByAccessToken_SessionExists() throws Exception {
        authenticatedUserRepository.save(authUser);

        mockMvc.perform(get("/api/v1/authentication")
                .cookie(createCookie(SSO_COOKIE_NAME, authUser.getAccessToken(), -1)))
                .andExpect(status().isOk());
    }
    @Test
    public void getByAccessToken_SessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/authentication")
                .cookie(createCookie(SSO_COOKIE_NAME, authUser.getAccessToken(), -1)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void getByAccessToken_NoCookie() throws Exception {
        mockMvc.perform(get("/api/v1/authentication"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_Successful() throws Exception {
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        AuthRequestDTO authRequestDTO = new AuthRequestDTOBuilder().build();

        Map<String, List<String>> authGroups = authGroupsService.getAuthGroups();

        stubFor(WireMock.post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroups.get(APP_CONFIG_AUTH_GROUP + ".admin").get(0))).build()))));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(authRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationTokenDTO authenticationTokenDTO = (AuthenticationTokenDTO) convertJSONStringToObject(mvcResult.getResponse().getContentAsString(), AuthenticationTokenDTO.class);

        Optional<AuthenticatedUser> authUser = authenticatedUserRepository.getByAccessToken(authenticationTokenDTO.getAccessToken());

        assertTrue(authUser.isPresent());
    }
    @Test
    public void login_ExistingSession() throws Exception {
        String emailAddress = randomEmailAddress();
        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        AuthRequestDTO authRequestDTO = new AuthRequestDTOBuilder().build();

        authenticatedUserRepository.save(new AuthenticatedUserBuilder().withUsername(authRequestDTO.getUsername()).build());

        Map<String, List<String>> authGroups = authGroupsService.getAuthGroups();

        stubFor(WireMock.post(urlEqualTo("/token?scope=openid%20panera"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(new Iso3ResponseBuilder().withId_token(buildIso3ResponseMap(emailAddress, firstName, lastName, authGroups.get(APP_CONFIG_AUTH_GROUP + ".admin").get(0))).build()))));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/authentication")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(authRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationTokenDTO authenticationTokenDTO = (AuthenticationTokenDTO) convertJSONStringToObject(mvcResult.getResponse().getContentAsString(), AuthenticationTokenDTO.class);

        Optional<AuthenticatedUser> authUser = authenticatedUserRepository.getByAccessToken(authenticationTokenDTO.getAccessToken());

        assertTrue(authUser.isPresent());
    }

    @Test
    public void logout_Successful() throws Exception {
        authenticatedUserRepository.save(authUser);

        mockMvc.perform(delete("/api/v1/authentication")
                .cookie(createCookie(SSO_COOKIE_NAME, authUser.getAccessToken(), -1)))
                .andExpect(status().isNoContent());

        Optional<AuthenticatedUser> optAuthUser = authenticatedUserRepository.getByAccessToken(authUser.getAccessToken());

        assertFalse(optAuthUser.isPresent());
    }
    @Test
    public void logout_SessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/authentication")
                .cookie(createCookie(SSO_COOKIE_NAME, authUser.getAccessToken(), -1)))
                .andExpect(status().isNoContent());

        Optional<AuthenticatedUser> optAuthUser = authenticatedUserRepository.getByAccessToken(authUser.getAccessToken());

        assertFalse(optAuthUser.isPresent());
    }
    @Test
    public void logout_NoCookie() throws Exception {
        mockMvc.perform(delete("/api/v1/authentication"))
                .andExpect(status().isNoContent());
    }
}