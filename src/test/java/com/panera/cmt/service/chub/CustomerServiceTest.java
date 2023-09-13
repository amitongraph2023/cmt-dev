package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.Customer;
import com.panera.cmt.dto.proxy.chub.CustomerDetails;
import com.panera.cmt.dto.proxy.chub.TaxExemption;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.enums.UpdateAccountStatusAction;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_builders.CustomerBuilder;
import com.panera.cmt.test_builders.TaxExemptionBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_CUSTOMER;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;

@ActiveProfiles("test")
public class CustomerServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @InjectMocks
    private CustomerService classUnderTest;

    @Mock
    private IAppConfigLocalService appConfigService;

    private String auditSubject = AUDIT_SUBJECT_CUSTOMER;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getCustomer_CustomerIsFound_ExpectOptionalOfCustomer() {
        Long id = random.nextLong();
        Map<String, Object> customer = new ChubFactoryUtil().extended(false, 0, 0).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        Optional<Customer> result = classUnderTest.getCustomer(id);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));

        assertTrue(result.isPresent());
        assertEquals(customer.get("customerId"), result.get().getCustomerId());
        assertEquals(customer.get("username"), result.get().getUsername());
        assertEquals(customer.get("firstName"), result.get().getFirstName());
        assertEquals(customer.get("lastName"), result.get().getLastName());
        assertEquals(customer.get("isEmailGlobalOpt"), result.get().isEmailGlobalOpt());
        assertEquals(customer.get("isSmsGlobalOpt"), result.get().isSmsGlobalOpt());
        assertEquals(customer.get("isMobilePushOpt"), result.get().isMobilePushOpt());
    }
    @Test
    public void getCustomer_EmptyCustomerHubResponse_ExpectEmptyOptional() {
        Long id = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        Optional<Customer> result = classUnderTest.getCustomer(id);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getCustomer_IdIsNull_ExpectEmptyOptional() {
        Map<String, Object> customer = new ChubFactoryUtil().extended(false, 0, 0).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        Optional<Customer> result = classUnderTest.getCustomer(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));

        assertFalse(result.isPresent());
    }

    @Test
    public void getCustomerDetails_CustomerIsFound_ExpectOptionalOfCustomer() {
        Long id = random.nextLong();
        String facebookId = randomAlphanumeric(15);
        String googleId = randomAlphanumeric(15);
        TaxExemption taxExemption = new TaxExemptionBuilder().build();
        Map<String, Object> customer = new ChubFactoryUtil().extended(true, 0, 0).withBirthday().withSocialIntegration(facebookId, googleId).withTaxExemption(taxExemption).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        Optional<CustomerDetails> result = classUnderTest.getCustomerDetails(id);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertTrue(result.isPresent());
        assertEquals(customer.get("customerId"), result.get().getCustomerId());
        assertEquals(customer.get("username"), result.get().getUsername());
        assertEquals(customer.get("firstName"), result.get().getFirstName());
        assertEquals(customer.get("lastName"), result.get().getLastName());
        assertEquals(customer.get("isEmailGlobalOpt"), result.get().isEmailGlobalOpt());
        assertEquals(customer.get("isSmsGlobalOpt"), result.get().isSmsGlobalOpt());
        assertEquals(customer.get("isMobilePushOpt"), result.get().isMobilePushOpt());
        assertEquals(((Map)customer.get("loyalty")).get("cardNumber"), result.get().getLoyaltyCardNumber());
        assertEquals(((Map)customer.get("birthDate")).get("birthDay") + "/" + ((Map)customer.get("birthDate")).get("birthMonth"), result.get().getDob());
        assertEquals(facebookId, result.get().getSocialIntegration().getFacebookIntegration().getFacebookId());
        assertEquals(googleId, result.get().getSocialIntegration().getGoogleIntegration().getGoogleId());
        assertEquals(1, result.get().getTaxExemptions().size());
        assertEquals(taxExemption.getCompany(), result.get().getTaxExemptions().get(0).getCompany());
        assertEquals(taxExemption.getState(), result.get().getTaxExemptions().get(0).getState());
        assertEquals(taxExemption.getCountry(), result.get().getTaxExemptions().get(0).getCountry());
    }
    @Test
    public void getCustomerDetails_EmptyCustomerHubResponse_ExpectEmptyOptional() {
        Long id = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse().withStatus(404)));

        Optional<CustomerDetails> result = classUnderTest.getCustomerDetails(id);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getCustomerDetails_IdIsNull_ExpectEmptyOptional() {
        String facebookId = randomAlphanumeric(15);
        String googleId = randomAlphanumeric(15);
        TaxExemption taxExemption = new TaxExemptionBuilder().build();
        Map<String, Object> customer = new ChubFactoryUtil().extended(true, 0, 0).withBirthday().withSocialIntegration(facebookId, googleId).withTaxExemption(taxExemption).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        Optional<CustomerDetails> result = classUnderTest.getCustomerDetails(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertFalse(result.isPresent());
    }

    @Test
    public void updateAccountStatus_ReinstateAccount() {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.REINSTATE;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.updateAccountStatus(id, action);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAccountStatus_SuspendAccount() {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.SUSPEND;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.updateAccountStatus(id, action);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAccountStatus_TerminateAccount() {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.TERMINATE;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .withRequestBody(matchingJsonPath("$.reason", containing("FRAUD")))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.updateAccountStatus(id, action);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .withRequestBody(matchingJsonPath("$.reason", containing("FRAUD"))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAccountStatus_IdIsNull() {
        UpdateAccountStatusAction action = UpdateAccountStatusAction.TERMINATE;
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.updateAccountStatus(null, action);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());
    }
    @Test
    public void updateAccountStatus_ActionIsNull() {
        Long id = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT)))
                .willReturn(aResponse().withStatus(statusCode)));

        classUnderTest.updateAccountStatus(id, null);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.MANAGE_ACCOUNT))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());
    }

    @Test
    public void updateCustomer_CustomerSuccessfullyUpdated_ExpectOptionalOfResponseHolderWithEntity() {
        Long id = random.nextLong();
        Customer updatedCustomer = new CustomerBuilder().build();
        int statusCode = 200;
        Map<String, Object> customerMap = new ChubFactoryUtil().extended(false, 0, 0).build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerMap))));

        Optional<ResponseHolder<Customer>> result = classUnderTest.updateCustomer(id, updatedCustomer);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.OK, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateCustomer_ValidationError_ExpectOptionalOfResponseHolderWithErrors() {
        Long id = random.nextLong();
        Customer updatedCustomer = new CustomerBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Customer>> result = classUnderTest.updateCustomer(id, updatedCustomer);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
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
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateCustomer_CustomerIsNotFound_ExpectOptionalOfResponseHolderWith404() {
        Long id = random.nextLong();
        Customer updatedCustomer = new CustomerBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Customer>> result = classUnderTest.updateCustomer(id, updatedCustomer);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateCustomer_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long id = random.nextLong();
        Customer updatedCustomer = new CustomerBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Customer>> result = classUnderTest.updateCustomer(id, updatedCustomer);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(id, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateCustomer_IdIsNull_ExpectEmptyOptional() {
        Customer updatedCustomer = new CustomerBuilder().build();
        int statusCode = 200;
        Map<String, Object> customerMap = new ChubFactoryUtil().extended(false, 0, 0).build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerMap))));

        Optional<ResponseHolder<Customer>> result = classUnderTest.updateCustomer(null, updatedCustomer);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateCustomer_UpdatedCustomerIsNull_ExpectEmptyOptional() {
        Long id = random.nextLong();
        int statusCode = 200;
        Map<String, Object> customerMap = new ChubFactoryUtil().extended(false, 0, 0).build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerMap))));

        Optional<ResponseHolder<Customer>> result = classUnderTest.updateCustomer(id, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
}