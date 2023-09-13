package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.CustomerSubscriptions;
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
import com.panera.cmt.test_builders.CustomerSubscriptionsBuilder;
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
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_SUBSCRIPTIONS;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@SuppressWarnings({"Duplicates", "unchecked"})
public class CustomerSubscriptionServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @InjectMocks
    private CustomerSubscriptionService classUnderTest;

    @Mock
    private IAppConfigLocalService appConfigService;

    private String auditSubject = AUDIT_SUBJECT_SUBSCRIPTIONS;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getSubscriptions_SubscriptionsAreFound_ExpectOptionalOfCustomerSubscriptions() {
        Long customerId = random.nextLong();
        Map<String, Object> subscriptions = new ChubFactoryUtil().buildAsSubscriptions();
        Map<String, Object> subscription = ((List<Map<String, Object>>)subscriptions.get("subscriptions")).get(0);
        Map<String, Object> suppressor = ((List<Map<String, Object>>)subscriptions.get("suppressors")).get(0);

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(subscriptions))));

        Optional<CustomerSubscriptions> result = classUnderTest.getSubscriptions(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));

        assertTrue(result.isPresent());
        assertNotNull(result.get().getSubscriptions());
        assertEquals(1, result.get().getSubscriptions().size());
        assertNotNull(subscription);
        assertEquals(subscription.get("subscriptionCode"), result.get().getSubscriptions().get(0).getSubscriptionCode());
        assertEquals(subscription.get("displayName"), result.get().getSubscriptions().get(0).getDisplayName());
        assertEquals(subscription.get("isSubscribed"), result.get().getSubscriptions().get(0).isSubscribed());
        assertEquals(1, result.get().getSuppressors().size());
        assertNotNull(suppressor);
        assertEquals(suppressor.get("suppressionCode"), result.get().getSuppressors().get(0).getSuppressionCode());
        assertEquals(suppressor.get("displayName"), result.get().getSuppressors().get(0).getDisplayName());
        assertEquals(suppressor.get("isSuppressed"), result.get().getSuppressors().get(0).isSuppressed());
    }
    @Test
    public void getSubscriptions_SubscriptionsAreNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(404)));

        Optional<CustomerSubscriptions> result = classUnderTest.getSubscriptions(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getSubscriptions_CustomerIdIsNull_ExpectEmptyOptional() {
        Map<String, Object> subscriptions = new ChubFactoryUtil().buildAsSubscriptions();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(subscriptions))));

        Optional<CustomerSubscriptions> result = classUnderTest.getSubscriptions(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));

        assertFalse(result.isPresent());
    }

    @Test
    public void updateSubscriptions_SuccessfulUpdate_ExpectOptionalOfResponseHolderWith204Status() {
        Long customerId = random.nextLong();
        CustomerSubscriptions subscriptions = new CustomerSubscriptionsBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CustomerSubscriptions>> result = classUnderTest.updateSubscriptions(customerId, subscriptions);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
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
    public void updateSubscriptions_CustomerIsNotFound_ExpectOptionalOfResponseHolderWith404Status() {
        Long customerId = random.nextLong();
        CustomerSubscriptions subscriptions = new CustomerSubscriptionsBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CustomerSubscriptions>> result = classUnderTest.updateSubscriptions(customerId, subscriptions);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
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
    public void updateSubscriptions_ValidationError_ExpectOptionalOfResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        CustomerSubscriptions subscriptions = new CustomerSubscriptionsBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<CustomerSubscriptions>> result = classUnderTest.updateSubscriptions(customerId, subscriptions);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
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
    public void updateSubscriptions_CustomerHubResponseIs500_ExpectOptionalOfResponseHolderWith500Status() {
        Long customerId = random.nextLong();
        CustomerSubscriptions subscriptions = new CustomerSubscriptionsBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CustomerSubscriptions>> result = classUnderTest.updateSubscriptions(customerId, subscriptions);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateSubscriptions_CustomerIdIsNull_ExpectEmptyOptional() {
        CustomerSubscriptions subscriptions = new CustomerSubscriptionsBuilder().build();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CustomerSubscriptions>> result = classUnderTest.updateSubscriptions(null, subscriptions);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateSubscriptions_PreferenceIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<CustomerSubscriptions>> result = classUnderTest.updateSubscriptions(customerId, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.SUBSCRIPTIONS))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
}