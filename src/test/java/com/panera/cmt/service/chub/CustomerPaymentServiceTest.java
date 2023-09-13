package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.*;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.PaymentOptionType;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.*;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_PAYMENT_OPTION;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ActiveProfiles("test")
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest(PaymentOptionType.class)
@RunWith(PowerMockRunner.class)
@SuppressWarnings({"Duplicates", "unchecked"})
public class CustomerPaymentServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @Mock
    private IAppConfigLocalService appConfigService;

    @InjectMocks
    private CustomerPaymentService classUnderTest;

    private String auditSubject = AUDIT_SUBJECT_PAYMENT_OPTION;
    private PaymentOptionType invalidType;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);

        PaymentOptionType[] origValues = PaymentOptionType.values();
        invalidType = PowerMockito.mock(PaymentOptionType.class);
        Whitebox.setInternalState(invalidType, "name", "-");
        Whitebox.setInternalState(invalidType, "ordinal", origValues.length);
        PaymentOptionType[] newValues = Arrays.copyOf(origValues, origValues.length+1);
        newValues[newValues.length-1] = invalidType;
        PowerMockito.mockStatic(PaymentOptionType.class);
        PowerMockito.when(PaymentOptionType.values()).thenReturn(newValues);
    }

    @Test
    public void addPaymentOption_AddApplePay_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.applepay;
        ApplePay entity = new ApplePayBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<ApplePay>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void addPaymentOption_AddCreditCard_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.creditcard;
        CreditCard entity = new CreditCardBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CreditCard>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void addPaymentOption_AddGiftCard_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
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
    public void addPaymentOption_AddPayPal_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.paypal;
        PayPal entity = new PayPalBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PayPal>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void addPaymentOption_CustomerIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
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
    public void addPaymentOption_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
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
    public void addPaymentOption_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.CREATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void addPaymentOption_CustomerIdIsNull_ExpectEmptyOptional() {
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(null, type, entity);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void addPaymentOption_TypeIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(customerId, null, entity);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void addPaymentOption_EntityIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.addPaymentOption(customerId, type, null);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void addPaymentOption_InvalidType_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = invalidType;
        CreditCard entity = new CreditCardBuilder().build();
        int statusCode = 201;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CreditCard>> result = classUnderTest.addPaymentOption(customerId, type, entity);

        verify(0, postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void deletePaymentOption_DeleteApplePay_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.paypal;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<ApplePay>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_DeleteCreditCard_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CreditCard>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_DeleteGiftCard_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_DeletePayPal_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.paypal;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PayPal>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_CustomerIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
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
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deletePaymentOption_CustomerIdIsNull_ExpectEmptyOptional() {
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode )));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(null, id, type);

        verify(0, deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void deletePaymentOption_IdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, null, type);

        verify(0, deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void deletePaymentOption_TypeIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, id, null);

        verify(0, deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void deletePaymentOption_TypeIsInvalid_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = invalidType;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.deletePaymentOption(customerId, id, type);

        verify(0, deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void getPaymentOption_GetApplePay_ExpectOptionalOfEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.applepay;
        Map<String, Object> response = new ChubFactoryUtil().buildAsApplePay();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<ApplePay> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertTrue(result.isPresent());
        assertEquals(response.get("accountNumber"), result.get().getAccountNumber());
    }
    @Test
    public void getPaymentOption_GetCreditCard_ExpectOptionalOfEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsCreditCard();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertTrue(result.isPresent());
        assertEquals(response.get("token"), result.get().getToken());
        assertEquals(response.get("expirationDate"), result.get().getExpirationDate());
        assertEquals(response.get("cardholderName"), result.get().getCardholderName());
        assertEquals(response.get("lastFour"), result.get().getLastFour());
        assertEquals(response.get("creditCardType"), result.get().getCreditCardType());
        assertEquals(response.get("paymentProcessor"), result.get().getPaymentProcessor());
        assertEquals(response.get("paymentLabel"), result.get().getPaymentLabel());
        assertEquals(response.get("isDefault"), result.get().isDefault());
    }
    @Test
    public void getPaymentOption_GetGiftCard_ExpectOptionalOfEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsGiftCard();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<GiftCard> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertTrue(result.isPresent());
        assertEquals(response.get("cardNumber"), result.get().getCardNumber());
        assertEquals(response.get("cardNickname"), result.get().getCardNickname());
    }
    @Test
    public void getPaymentOption_GetPayPal_ExpectOptionalOfEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.paypal;
        Map<String, Object> response = new ChubFactoryUtil().buildAsPayPal();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<PayPal> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertTrue(result.isPresent());
        assertEquals(response.get("accountNumber"), result.get().getAccountNumber());
        assertEquals(response.get("username"), result.get().getUsername());
    }
    @Test
    public void getPaymentOption_CustomerIsNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOption_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(500)));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOption_CustomerIdIsNull_ExpectEmptyOptional() {
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsCreditCard();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(null, id, type);

        verify(0, getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOption_IdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.creditcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsCreditCard();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(customerId, null, type);

        verify(0, getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOption_IsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsCreditCard();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(customerId, id, null);

        verify(0, getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOption_InvalidType_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = invalidType;
        Map<String, Object> response = new ChubFactoryUtil().buildAsCreditCard();

        stubFor(get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<CreditCard> result = classUnderTest.getPaymentOption(customerId, id, type);

        verify(0, getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));

        assertFalse(result.isPresent());
    }

    @Test
    public void getPaymentOptions_PaymentOptionsAreFound_ExpectOptionalOfPaymentOptions() {
        Long customerId = random.nextLong();
        Map<String, Object> response = new ChubFactoryUtil().buildAsPaymentOptions();
        Map<String, Object> cca = (Map) ((List)response.get("corporateCateringAccounts")).get(0);
        Map<String, Object> creditCard = (Map) ((List)response.get("creditCards")).get(0);
        Map<String, Object> giftCard = (Map) ((List)response.get("giftCards")).get(0);
        Map<String, Object> payPal = (Map) ((List)response.get("payPals")).get(0);

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<PaymentOptions> result = classUnderTest.getPaymentOptions(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE))));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getCorporateCateringAccounts().size());
        assertEquals(cca.get("orgNumber"), result.get().getCorporateCateringAccounts().get(0).getOrgNumber());
        assertEquals(cca.get("ccaNumber"), result.get().getCorporateCateringAccounts().get(0).getCcaNumber());
        assertEquals(cca.get("ccaBillingName"), result.get().getCorporateCateringAccounts().get(0).getCcaBillingName());
        assertEquals(cca.get("addressLine1"), result.get().getCorporateCateringAccounts().get(0).getAddressLine1());
        assertEquals(cca.get("addressLine2"), result.get().getCorporateCateringAccounts().get(0).getAddressLine2());
        assertEquals(cca.get("city"), result.get().getCorporateCateringAccounts().get(0).getCity());
        assertEquals(cca.get("state"), result.get().getCorporateCateringAccounts().get(0).getState());
        assertEquals(cca.get("zipCode"), result.get().getCorporateCateringAccounts().get(0).getZipCode());
        assertEquals(cca.get("country"), result.get().getCorporateCateringAccounts().get(0).getCountry());
        assertEquals(cca.get("poRequired"), result.get().getCorporateCateringAccounts().get(0).getPoRequired());
        assertEquals(cca.get("onlineEnabled"), result.get().getCorporateCateringAccounts().get(0).getOnlineEnabled());
        assertEquals(1, result.get().getCreditCards().size());
        assertEquals(creditCard.get("token"), result.get().getCreditCards().get(0).getToken());
        assertEquals(creditCard.get("expirationDate"), result.get().getCreditCards().get(0).getExpirationDate());
        assertEquals(creditCard.get("cardholderName"), result.get().getCreditCards().get(0).getCardholderName());
        assertEquals(creditCard.get("lastFour"), result.get().getCreditCards().get(0).getLastFour());
        assertEquals(creditCard.get("creditCardType"), result.get().getCreditCards().get(0).getCreditCardType());
        assertEquals(creditCard.get("paymentProcessor"), result.get().getCreditCards().get(0).getPaymentProcessor());
        assertEquals(creditCard.get("paymentLabel"), result.get().getCreditCards().get(0).getPaymentLabel());
        assertEquals(creditCard.get("isDefault"), result.get().getCreditCards().get(0).isDefault());
        assertEquals(1, result.get().getGiftCards().size());
        assertEquals(giftCard.get("cardNumber"), result.get().getGiftCards().get(0).getCardNumber());
        assertEquals(giftCard.get("cardNickname"), result.get().getGiftCards().get(0).getCardNickname());
        assertEquals(1, result.get().getPayPals().size());
        assertEquals(payPal.get("accountNumber"), result.get().getPayPals().get(0).getAccountNumber());
        assertEquals(payPal.get("username"), result.get().getPayPals().get(0).getUsername());
    }
    @Test
    public void getPaymentOptions_CustomerIsNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE)))
                .willReturn(aResponse().withStatus(404)));

        Optional<PaymentOptions> result = classUnderTest.getPaymentOptions(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOptions_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE)))
                .willReturn(aResponse().withStatus(500)));

        Optional<PaymentOptions> result = classUnderTest.getPaymentOptions(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getPaymentOptions_CustomerIdIsNull_ExpectEmptyOptional() {
        Map<String, Object> response = new ChubFactoryUtil().buildAsPaymentOptions();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        Optional<PaymentOptions> result = classUnderTest.getPaymentOptions(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE))));

        assertFalse(result.isPresent());
    }

    @Test
    public void updatePaymentOption_UpdateCreditCard_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        CreditCard entity = new CreditCardBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CreditCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePaymentOption_UpdateGiftCard_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePaymentOption_UpdatePayPal_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.paypal;
        PayPal entity = new PayPalBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<PayPal>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePaymentOption_CustomerOrPaymentOptionsIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePaymentOption_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
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
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePaymentOption_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updatePaymentOption_CustomerIdIsNull_ExpectEmptyOptional() {
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(null, id, type, entity);

        verify(0, putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updatePaymentOption_IdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, null, type, entity);

        verify(0, putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updatePaymentOption_TypeIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        GiftCard entity = new GiftCardBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, id, null, entity);

        verify(0, putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updatePaymentOption_EntityIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<GiftCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, null);

        verify(0, putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updatePaymentOption_InvalidType_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = invalidType;
        CreditCard entity = new CreditCardBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CreditCard>> result = classUnderTest.updatePaymentOption(customerId, id, type, entity);

        verify(0, putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    private String transformPaymentOptionsEndpoint(ChubEndpoints endpoint, PaymentOptionType type) {
        return endpoint.getStub().replaceAll("\\{type\\}", (type.equals(invalidType)) ? "invalid" : type.name()).replaceAll("\\{.*?\\}", "(.*)");
    }
}