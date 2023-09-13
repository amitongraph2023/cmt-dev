package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.proxy.chub.Address;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ActionType;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.Audit;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.mongo.repository.IAuditRepository;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AddressBuilder;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
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
import static com.panera.cmt.config.Constants.AUDIT_SUBJECT_ADDRESS;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@SuppressWarnings("Duplicates")
public class CustomerAddressServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Mock
    private IAuditRepository auditRepository;

    @Mock
    private IAppConfigLocalService appConfigService;

    @InjectMocks
    private CustomerAddressService classUnderTest;

    private String auditSubject = AUDIT_SUBJECT_ADDRESS;
    private Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void createAddress_AddressSuccessfullyCreated_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 201;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.createAddress(customerId, address);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
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
    public void createAddress_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Address>> result = classUnderTest.createAddress(customerId, address);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
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
    public void createAddress_CustomerIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.createAddress(customerId, address);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
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
    public void createAddress_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.createAddress(customerId, address);

        verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.CREATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void createAddress_CustomerIdIsNull_ExpectEmptyOptional() {
        Address address = new AddressBuilder().build();
        int statusCode = 201;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.createAddress(null, address);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void createAddress_AddressIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 201;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.createAddress(customerId, null);

        verify(0, postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void deleteAddress_AddressSuccessfullyDeleted_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.deleteAddress(customerId, addressId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deleteAddress_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Address>> result = classUnderTest.deleteAddress(customerId, addressId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
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
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deleteAddress_CustomerOrAddressIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.deleteAddress(customerId, addressId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deleteAddress_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.deleteAddress(customerId, addressId);

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.DELETE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void deleteAddress_CustomerIdIsNull_ExpectEmptyOptional() {
        Long addressId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.deleteAddress(null, addressId);

        verify(0, deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void deleteAddress_AddressIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        int statusCode = 204;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.deleteAddress(customerId, null);

        verify(0, deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }

    @Test
    public void getAddress_AddressIsFound_ExpectOptionalIfAddress() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Map<String, Object> address = new ChubFactoryUtil().buildAsAddress();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(address))));

        Optional<Address> result = classUnderTest.getAddress(customerId, addressId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));

        assertTrue(result.isPresent());
        assertEquals(address.get("id"), result.get().getId());
        assertEquals(address.get("name"), result.get().getName());
        assertEquals(address.get("contactPhone"), result.get().getContactPhone());
        assertEquals(address.get("phoneExtension"), result.get().getPhoneExtension());
        assertEquals(address.get("additionalInfo"), result.get().getAdditionalInfo());
        assertEquals(address.get("addressLine1"), result.get().getAddressLine1());
        assertEquals(address.get("addressLine2"), result.get().getAddressLine2());
        assertEquals(address.get("city"), result.get().getCity());
        assertEquals(address.get("state"), result.get().getState());
        assertEquals(address.get("country"), result.get().getCountry());
        assertEquals(address.get("zip"), result.get().getZip());
        assertEquals(address.get("addressType"), result.get().getAddressType());
        assertEquals(address.get("isDefault"), result.get().isDefault());
    }
    @Test
    public void getAddress_AddressIsNotFound_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        Optional<Address> result = classUnderTest.getAddress(customerId, addressId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getAddress_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        Optional<Address> result = classUnderTest.getAddress(customerId, addressId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getAddress_CustomerIdIsNull_ExpectEmptyOptional() {
        Long addressId = random.nextLong();
        Map<String, Object> address = new ChubFactoryUtil().buildAsAddress();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(address))));

        Optional<Address> result = classUnderTest.getAddress(null, addressId);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));

        assertFalse(result.isPresent());
    }
    @Test
    public void getAddress_AddressIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Map<String, Object> address = new ChubFactoryUtil().buildAsAddress();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(address))));

        Optional<Address> result = classUnderTest.getAddress(customerId, null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));

        assertFalse(result.isPresent());
    }

    @Test
    public void getAddresses_AddressIsFound_ExpectOptionalOfAddress() {
        Long customerId = random.nextLong();
        Map<String, Object> address = new ChubFactoryUtil().buildAsAddress();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(address)))));

        Optional<List<Address>> result = classUnderTest.getAddresses(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(address.get("id"), result.get().get(0).getId());
        assertEquals(address.get("name"), result.get().get(0).getName());
        assertEquals(address.get("contactPhone"), result.get().get(0).getContactPhone());
        assertEquals(address.get("phoneExtension"), result.get().get(0).getPhoneExtension());
        assertEquals(address.get("additionalInfo"), result.get().get(0).getAdditionalInfo());
        assertEquals(address.get("addressLine1"), result.get().get(0).getAddressLine1());
        assertEquals(address.get("addressLine2"), result.get().get(0).getAddressLine2());
        assertEquals(address.get("city"), result.get().get(0).getCity());
        assertEquals(address.get("state"), result.get().get(0).getState());
        assertEquals(address.get("country"), result.get().get(0).getCountry());
        assertEquals(address.get("zip"), result.get().get(0).getZip());
        assertEquals(address.get("addressType"), result.get().get(0).getAddressType());
        assertEquals(address.get("isDefault"), result.get().get(0).isDefault());
    }
    @Test
    public void getAddresses_AddressIsNotFound_ExpectOptionalOfEmptyArray() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(404)));

        Optional<List<Address>> result = classUnderTest.getAddresses(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }
    @Test
    public void getAddresses_CustomerHubResponseIs500_ExpectOptionalOfEmptyArray() {
        Long customerId = random.nextLong();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(500)));

        Optional<List<Address>> result = classUnderTest.getAddresses(customerId);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }
    @Test
    public void getAddresses_CustomerIdIsNull_ExpectEmptyOptional() {
        Map<String, Object> address = new ChubFactoryUtil().buildAsAddress();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(address)))));

        Optional<List<Address>> result = classUnderTest.getAddresses(null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));

        assertFalse(result.isPresent());
    }

    @Test
    public void updateAddress_AddressSuccessfullyUpdated_ExpectOptionalOfResponseHolderWithEntity() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 200;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(customerId, addressId, address);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.OK, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAddress_ValidationError_ExpectOptionalResponseHolderWithErrors() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 406;
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(customerId, addressId, address);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
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
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAddress_CustomerOrAddressIsNotFound_ExpectOptionalResponseHolderWith404() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 404;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(customerId, addressId, address);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertTrue(result.isPresent());
        assertEquals(HttpStatus.NOT_FOUND, result.get().getStatus());
        assertNull(result.get().getErrors());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAddress_CustomerHubResponseIs500_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 500;
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(statusCode)));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(customerId, addressId, address);

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(1)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());

        assertEquals(ActionType.UPDATE, argumentCaptor.getValue().getActionType());
        assertEquals(auditSubject, argumentCaptor.getValue().getSubject());
        assertEquals(customerId, argumentCaptor.getValue().getPersonId());
        assertEquals(addressId, argumentCaptor.getValue().getObjectId());
        assertEquals(statusCode, (int) argumentCaptor.getValue().getStatusCode());
    }
    @Test
    public void updateAddress_CustomerIdIsNull_ExpectEmptyOptional() {
        Long addressId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 200;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(null, addressId, address);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateAddress_AddressIdIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Address address = new AddressBuilder().build();
        int statusCode = 200;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(customerId, null, address);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
    @Test
    public void updateAddress_UpdatedAddressIsNull_ExpectEmptyOptional() {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        int statusCode = 200;
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();
        ArgumentCaptor<Audit> argumentCaptor = ArgumentCaptor.forClass(Audit.class);

        stubFor(put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        Optional<ResponseHolder<Address>> result = classUnderTest.updateAddress(customerId, addressId, null);

        verify(0, putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
        Mockito.verify(auditRepository, Mockito.times(0)).save(argumentCaptor.capture());

        assertFalse(result.isPresent());
    }
}