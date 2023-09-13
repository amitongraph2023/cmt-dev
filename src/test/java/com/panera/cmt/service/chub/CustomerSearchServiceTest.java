package com.panera.cmt.service.chub;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.dto.proxy.chub.SearchCustomer;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.CustomerSearchType;
import com.panera.cmt.enums.Role;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_builders.AuthenticatedUserBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.randomEmailAddress;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
@SuppressWarnings("unchecked")
public class CustomerSearchServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @InjectMocks
    private CustomerSearchService classUnderTest;
    
    @Mock
    private IAppConfigLocalService appConfigService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(classUnderTest, "baseUrl", "http://localhost:" + wireMockRule.port());

        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void searchCustomer_SearchByEmail_CustomerIsFound_ExpectListOfCustomer() {
        CustomerSearchType type = CustomerSearchType.email;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);

        Map<String, Object> customer = new ChubFactoryUtil().searchResult(emailAddress, phoneNumber).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(customer)))));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(1, result.getEntity().size());
        assertEquals(customer.get("customerId"), result.getEntity().get(0).getCustomerId());
        assertEquals(customer.get("username").toString(), result.getEntity().get(0).getUsername());
        assertEquals(customer.get("firstName").toString(), result.getEntity().get(0).getFirstName());
        assertEquals(customer.get("lastName").toString(), result.getEntity().get(0).getLastName());
        assertEquals(emailAddress, result.getEntity().get(0).getDefaultEmail());
        assertEquals(phoneNumber, result.getEntity().get(0).getDefaultPhone());
    }
    @Test
    public void searchCustomer_SearchByEmail_CustomerIsNotFound_ExpectEmptyList() {
        CustomerSearchType type = CustomerSearchType.email;
        String value = UUID.randomUUID().toString();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(emptyList()))));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
    @Test
    public void searchCustomer_SearchByEmail_ValidationError_ExpectEmptyList() {
        CustomerSearchType type = CustomerSearchType.email;
        String value = UUID.randomUUID().toString();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(new AllErrorsDTOBuilder().build()))));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
    @Test
    public void searchCustomer_SearchByUsername_CustomerIsFound_ExpectListOfCustomer() {
        CustomerSearchType type = CustomerSearchType.username;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);

        Map<String, Object> customer = new ChubFactoryUtil().build();
        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(1, result.getEntity().size());
        assertEquals(customerDetails.get("customerId"), result.getEntity().get(0).getCustomerId());
        assertEquals(customerDetails.get("username").toString(), result.getEntity().get(0).getUsername());
        assertEquals(customerDetails.get("firstName").toString(), result.getEntity().get(0).getFirstName());
        assertEquals(customerDetails.get("lastName").toString(), result.getEntity().get(0).getLastName());
        assertEquals(emailAddress, result.getEntity().get(0).getDefaultEmail());
        assertEquals(phoneNumber, result.getEntity().get(0).getDefaultPhone());
    }
    @Test
    public void searchCustomer_SearchByUsername_CustomerIsNotFound_ExpectEmptyList() {
        CustomerSearchType type = CustomerSearchType.username;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);

        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("")));

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
    @Test
    public void searchCustomer_SearchByUsername_CustomerIsNotFound_EmptyResponseBody_ExpectEmptyList() {
        CustomerSearchType type = CustomerSearchType.username;
        String value = UUID.randomUUID().toString();
        String emailAddress = randomEmailAddress();
        String phoneNumber = randomNumeric(10);

        Map<String, Object> customerDetails = new ChubFactoryUtil().extended().withEmail(emailAddress).withPhone(phoneNumber).build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse().withBody("")));

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customerDetails))));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
    @Test
    public void searchCustomer_SearchByUsername_CustomerDetailsIsNotFound_ExpectEmptyList() {
        CustomerSearchType type = CustomerSearchType.username;
        String value = UUID.randomUUID().toString();

        Map<String, Object> customer = new ChubFactoryUtil().build();

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(customer))));

        stubFor(get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS)))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("")));

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, value);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
    @Test
    public void searchCustomer_TypeIsNull_ExpectEmptyList() {
        String value = UUID.randomUUID().toString();

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(null, value);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
    @Test
    public void searchCustomer_ValueIsNull_ExpectEmptyList() {
        CustomerSearchType type = CustomerSearchType.email;

        ResponseHolder<List<SearchCustomer>> result = classUnderTest.searchCustomer(type, null);

        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_SEARCH))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.USERNAME_LOOKUP))));
        verify(0, getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_DETAILS))));

        assertNotNull(result.getEntity());
        assertEquals(0, result.getEntity().size());
    }
}