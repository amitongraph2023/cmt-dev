package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PASSWORD;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.junit.Assert.*;

@ActiveProfiles("test")
public class CustomerPasswordServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

@Mock
private IAppConfigLocalService appConfigService;

    @InjectMocks
    private CustomerPasswordService classUnderTest;

    private String auditSubject = AUDIT_SUBJECT_PASSWORD;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }


    @Test
    @Ignore
    public void generatePassword_PasswordChangeSuccessful_ExpectOptionalOfNewPassword() {
        Long customerId = random.nextLong();

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SET)))
                .willReturn(aResponse().withStatus(204)));

        Optional<ResponseHolder<String>> result = classUnderTest.generatePassword(customerId);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getEntity());

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SET)))
                .withRequestBody(matchingJsonPath("$.newPassword", containing(result.get().getEntity()))));
    }
    @Test
    @Ignore
    public void generatePassword_CustomerIdIsNull_ExpectEmptyOptional() {
        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SET)))
                .willReturn(aResponse().withStatus(204)));

        Optional<ResponseHolder<String>> result = classUnderTest.generatePassword(null);

        assertFalse(result.isPresent());
    }


    @Test
    public void sendResetPasswordEmail() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SEND_RESET)))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.sendResetPasswordEmail(customerId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SEND_RESET))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void sendResetPasswordEmail_CustomerIdIsNull() {
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SEND_RESET)))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.sendResetPasswordEmail(null);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PASSWORD_SEND_RESET))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());
    }
}