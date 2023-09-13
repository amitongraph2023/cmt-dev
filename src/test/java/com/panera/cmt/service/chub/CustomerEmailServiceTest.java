package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.Email;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_builders.EmailBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_EMAIL;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@SuppressWarnings("Duplicates")
public class CustomerEmailServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @Mock
    private IAppConfigLocalService appConfigService;

    @InjectMocks
    private CustomerEmailService classUnderTest;

    private String auditSubject = AUDIT_SUBJECT_EMAIL;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getEmail_EmailIsFound_ExpectOptionalIfEmail() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(email))));

        Optional<Email> result = classUnderTest.getEmail(customerId, emailId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));

        assertTrue(result.isPresent());
        assertEquals(email.get("id"), result.get().getId());
        assertEquals(email.get("emailAddress"), result.get().getEmailAddress());
        assertEquals(email.get("emailType"), result.get().getEmailType());
        assertEquals(email.get("isDefault"), result.get().isDefault());
        assertEquals(email.get("isOpt"), result.get().isOpt());
        assertEquals(email.get("isVerified"), result.get().isVerified());
    }
    @Test
    public void getEmail_EmailIsNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        Optional<Email> result = classUnderTest.getEmail(customerId, emailId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getEmail_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        Optional<Email> result = classUnderTest.getEmail(customerId, emailId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getEmail_CustomerIdIsNull_ExpectEmptyOptional() {
        Long emailId = random.nextLong();
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(email))));

        Optional<Email> result = classUnderTest.getEmail(null, emailId);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getEmail_EmailIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(email))));

        Optional<Email> result = classUnderTest.getEmail(customerId, null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));

        assertFalse(result.isPresent());
    }

    @Test
    public void getEmails_EmailIsFound_ExpectOptionalOfEmail() {
        Long customerId = random.nextLong();
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(email)))));

        Optional<List<Email>> result = classUnderTest.getEmails(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(email.get("id"), result.get().get(0).getId());
        assertEquals(email.get("emailAddress"), result.get().get(0).getEmailAddress());
        assertEquals(email.get("emailType"), result.get().get(0).getEmailType());
        assertEquals(email.get("isDefault"), result.get().get(0).isDefault());
        assertEquals(email.get("isOpt"), result.get().get(0).isOpt());
        assertEquals(email.get("isVerified"), result.get().get(0).isVerified());
    }
    @Test
    public void getEmails_EmailIsNotFound_ExpectOptionalOfEmptyArray() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse().withStatus(404)));

        Optional<List<Email>> result = classUnderTest.getEmails(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }
    @Test
    public void getEmails_CustomerHubResponseIs500_ExpectOptionalOfEmptyArray() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse().withStatus(500)));

        Optional<List<Email>> result = classUnderTest.getEmails(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }
    @Test
    public void getEmails_CustomerIdIsNull_ExpectEmptyOptional() {
        Map<String, Object> email = new ChubFactoryUtil().withEmail(randomNumeric(10)).buildAsEmail();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(email)))));

        Optional<List<Email>> result = classUnderTest.getEmails(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BASE))));

        assertFalse(result.isPresent());
    }

    @Test
    public void setDefault_EmailSuccessfullySetAsDefault_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.setDefault(customerId, emailId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        int statusCode = 406;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Email>> result = classUnderTest.setDefault(customerId, emailId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_CustomerOrEmailIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.setDefault(customerId, emailId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.setDefault(customerId, emailId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_CustomerIdIsNull_ExpectEmptyOptional() {
        Long emailId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.setDefault(null, emailId);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void setDefault_EmailIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.setDefault(customerId, null);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void updateEmail_EmailSuccessfullyUpdated_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Email email = new EmailBuilder().build();
        int statusCode = 200;
        Map<String, Object> emailResponse = new ChubFactoryUtil().buildAsEmail();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emailResponse))));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(customerId, emailId, email);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.OK, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateEmail_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Email email = new EmailBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(customerId, emailId, email);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateEmail_CustomerOrEmailIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Email email = new EmailBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(customerId, emailId, email);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateEmail_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        Email email = new EmailBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(customerId, emailId, email);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(emailId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateEmail_CustomerIdIsNull_ExpectEmptyOptional() {
        Long emailId = random.nextLong();
        Email email = new EmailBuilder().build();
        int statusCode = 200;
        Map<String, Object> emailResponse = new ChubFactoryUtil().buildAsEmail();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emailResponse))));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(null, emailId, email);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateEmail_EmailIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Email email = new EmailBuilder().build();
        int statusCode = 200;
        Map<String, Object> emailResponse = new ChubFactoryUtil().buildAsEmail();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emailResponse))));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(customerId, null, email);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateEmail_UpdatedEmailIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long emailId = random.nextLong();
        int statusCode = 200;
        Map<String, Object> emailResponse = new ChubFactoryUtil().buildAsEmail();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emailResponse))));

        Optional<ResponseHolder<Email>> result = classUnderTest.updateEmail(customerId, emailId, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_EMAIL_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
}