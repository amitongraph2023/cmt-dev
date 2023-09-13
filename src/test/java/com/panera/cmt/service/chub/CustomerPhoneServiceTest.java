package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.Phone;
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
import com.panera.cmt.test_builders.PhoneBuilder;
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
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PHONE;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@SuppressWarnings("Duplicates")
public class CustomerPhoneServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @Mock
    private IAppConfigLocalService appConfigService;

    @InjectMocks
    private CustomerPhoneService classUnderTest;


    private String auditSubject = AUDIT_SUBJECT_PHONE;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void createPhone_PhoneSuccessfullyCreated_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 201;
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.createPhone(customerId, phone);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.CREATED, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.CREATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void createPhone_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.createPhone(customerId, phone);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.CREATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void createPhone_CustomerIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.createPhone(customerId, phone);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.CREATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void createPhone_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.createPhone(customerId, phone);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.CREATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void createPhone_CustomerIdIsNull_ExpectEmptyOptional() {
        Phone phone = new PhoneBuilder().build();
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.createPhone(null, phone);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void createPhone_PhoneIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.createPhone(customerId, null);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void deletePhone_PhoneSuccessfullyDeleted_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.deletePhone(customerId, phoneId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePhone_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.deletePhone(customerId, phoneId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.get().getStatus());
        assertNotNull(result.get().getErrors());
        assertEquals(errors.getErrors().size(), result.get().getErrors().getErrors().size());
        assertEquals(errors.getErrors().get(0).getDescription(), result.get().getErrors().getErrors().get(0).getDescription());
        assertEquals(errors.getErrors().get(0).getDetails(), result.get().getErrors().getErrors().get(0).getDetails());
        assertEquals(errors.getErrors().get(0).getReasonCode(), result.get().getErrors().getErrors().get(0).getReasonCode());
        assertEquals(errors.getErrors().get(0).getSource(), result.get().getErrors().getErrors().get(0).getSource());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePhone_CustomerOrPhoneIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.deletePhone(customerId, phoneId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePhone_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.deletePhone(customerId, phoneId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePhone_CustomerIdIsNull_ExpectEmptyOptional() {
        Long phoneId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.deletePhone(null, phoneId);

        verify(0, deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void deletePhone_PhoneIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.deletePhone(customerId, null);

        verify(0, deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void getPhone_PhoneIsFound_ExpectOptionalOfPhone() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phone))));

        Optional<Phone> result = classUnderTest.getPhone(customerId, phoneId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));

        assertTrue(result.isPresent());
        assertEquals(phone.get("id"), result.get().getId());
        assertEquals(phone.get("phoneNumber"), result.get().getPhoneNumber());
        assertEquals(phone.get("phoneType"), result.get().getPhoneType());
        assertEquals(phone.get("countryCode"), result.get().getCountryCode());
        assertEquals(phone.get("extension"), result.get().getExtension());
        assertEquals(phone.get("name"), result.get().getName());
        assertEquals(phone.get("isCallOpt"), result.get().isCallOpt());
        assertEquals(phone.get("isDefault"), result.get().isDefault());
        assertEquals(phone.get("isValid"), result.get().isValid());
    }
    @Test
    public void getPhone_PhoneIsNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        Optional<Phone> result = classUnderTest.getPhone(customerId, phoneId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPhone_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        Optional<Phone> result = classUnderTest.getPhone(customerId, phoneId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPhone_CustomerIdIsNull_ExpectEmptyOptional() {
        Long phoneId = random.nextLong();
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phone))));

        Optional<Phone> result = classUnderTest.getPhone(null, phoneId);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPhone_PhoneIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phone))));

        Optional<Phone> result = classUnderTest.getPhone(customerId, null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));

        assertFalse(result.isPresent());
    }

    @Test
    public void getPhones_PhoneIsFound_ExpectOptionalOfPhone() {
        Long customerId = random.nextLong();
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(phone)))));

        Optional<List<Phone>> result = classUnderTest.getPhones(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(phone.get("id"), result.get().get(0).getId());
        assertEquals(phone.get("phoneNumber"), result.get().get(0).getPhoneNumber());
        assertEquals(phone.get("phoneType"), result.get().get(0).getPhoneType());
        assertEquals(phone.get("countryCode"), result.get().get(0).getCountryCode());
        assertEquals(phone.get("extension"), result.get().get(0).getExtension());
        assertEquals(phone.get("name"), result.get().get(0).getName());
        assertEquals(phone.get("isCallOpt"), result.get().get(0).isCallOpt());
        assertEquals(phone.get("isDefault"), result.get().get(0).isDefault());
        assertEquals(phone.get("isValid"), result.get().get(0).isValid());
    }
    @Test
    public void getPhones_PhoneIsNotFound_ExpectOptionalOfEmptyArray() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(404)));

        Optional<List<Phone>> result = classUnderTest.getPhones(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }
    @Test
    public void getPhones_CustomerHubResponseIs500_ExpectOptionalOfEmptyArray() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse().withStatus(500)));

        Optional<List<Phone>> result = classUnderTest.getPhones(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }
    @Test
    public void getPhones_CustomerIdIsNull_ExpectEmptyOptional() {
        Map<String, Object> phone = new ChubFactoryUtil().withPhone(randomNumeric(10)).buildAsPhone();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(phone)))));

        Optional<List<Phone>> result = classUnderTest.getPhones(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BASE))));

        assertFalse(result.isPresent());
    }

    @Test
    public void setDefault_PhoneSuccessfullySetAsDefault_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.setDefault(customerId, phoneId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.setDefault(customerId, phoneId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
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
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_CustomerOrPhoneIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.setDefault(customerId, phoneId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.setDefault(customerId, phoneId);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void setDefault_CustomerIdIsNull_ExpectEmptyOptional() {
        Long phoneId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.setDefault(null, phoneId);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void setDefault_PhoneIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.setDefault(customerId, null);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_SET_DEFAULT))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void updatePhone_PhoneSuccessfullyUpdated_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 200;
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(customerId, phoneId, phone);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.OK, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePhone_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(customerId, phoneId, phone);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
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
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePhone_CustomerOrPhoneIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(customerId, phoneId, phone);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePhone_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(customerId, phoneId, phone);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(phoneId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePhone_CustomerIdIsNull_ExpectEmptyOptional() {
        Long phoneId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 200;
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(null, phoneId, phone);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updatePhone_PhoneIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Phone phone = new PhoneBuilder().build();
        int statusCode = 200;
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(customerId, null, phone);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updatePhone_UpdatedPhoneIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long phoneId = random.nextLong();
        int statusCode = 200;
        Map<String, Object> phoneResponse = new ChubFactoryUtil().buildAsPhone();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(phoneResponse))));

        Optional<ResponseHolder<Phone>> result = classUnderTest.updatePhone(customerId, phoneId, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_PHONE_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
}