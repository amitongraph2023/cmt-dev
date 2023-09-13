package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.chub.AddressDTO;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.service.chub.ICustomerAddressService;
import com.panera.cmt.test_builders.AddressDTOBuilder;
import com.panera.cmt.test_builders.AllErrorsDTOBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@Transactional
public class CustomerAddressIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerAddressService customerAddressService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private Random random = new Random();

    @MockBean(name = "paytronixApigeeRestTemplate")
    private RestTemplate paytronixRestTemplate;

    @MockBean
    private SSOController ssoController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        ReflectionTestUtils.setField(customerAddressService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void addAddress_SuccessfullyCreated_Expect201() throws Exception {
        Long customerId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
    }
    @Test
    public void addAddress_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
    }
    @Test
    public void addAddress_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
    }
    @Test
    public void addAddress_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/address", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(postRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
    }

    @Test
    public void deleteAddress_SuccessfullyDeleted_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNoContent());

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void deleteAddress_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNotFound());

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void deleteAddress_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void deleteAddress_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        stubFor(WireMock.delete(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isInternalServerError());

        verify(deleteRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }

    @Test
    public void getAddress_AddressIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        Map<String, Object> address = new ChubFactoryUtil().withAddress().buildAsAddress(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(address))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", isIntLong((Long)address.get("id"))))
                .andExpect(jsonPath("$.name", is(address.get("name"))))
                .andExpect(jsonPath("$.contactPhone", is(address.get("contactPhone"))))
                .andExpect(jsonPath("$.phoneExtension", is(address.get("phoneExtension"))))
                .andExpect(jsonPath("$.additionalInfo", is(address.get("additionalInfo"))))
                .andExpect(jsonPath("$.addressLine1", is(address.get("addressLine1"))))
                .andExpect(jsonPath("$.addressLine2", is(address.get("addressLine2"))))
                .andExpect(jsonPath("$.city", is(address.get("city"))))
                .andExpect(jsonPath("$.state", is(address.get("state"))))
                .andExpect(jsonPath("$.country", is(address.get("country"))))
                .andExpect(jsonPath("$.zip", is(address.get("zip"))))
                .andExpect(jsonPath("$.addressType", is(address.get("addressType"))))
                .andExpect(jsonPath("$.isDefault", is(address.get("isDefault"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void getAddress_AddressIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address/%d", customerId, addressId)))
                .andExpect(status().isNotFound());

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }

    @Test
    public void getAddresses_AddressIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Map<String, Object> address = new ChubFactoryUtil().withAddress().buildAsAddress(true);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(singletonList(address)))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIntLong((Long)address.get("id"))))
                .andExpect(jsonPath("$[0].name", is(address.get("name"))))
                .andExpect(jsonPath("$[0].contactPhone", is(address.get("contactPhone"))))
                .andExpect(jsonPath("$[0].phoneExtension", is(address.get("phoneExtension"))))
                .andExpect(jsonPath("$[0].additionalInfo", is(address.get("additionalInfo"))))
                .andExpect(jsonPath("$[0].addressLine1", is(address.get("addressLine1"))))
                .andExpect(jsonPath("$[0].addressLine2", is(address.get("addressLine2"))))
                .andExpect(jsonPath("$[0].city", is(address.get("city"))))
                .andExpect(jsonPath("$[0].state", is(address.get("state"))))
                .andExpect(jsonPath("$[0].country", is(address.get("country"))))
                .andExpect(jsonPath("$[0].zip", is(address.get("zip"))))
                .andExpect(jsonPath("$[0].addressType", is(address.get("addressType"))))
                .andExpect(jsonPath("$[0].isDefault", is(address.get("isDefault"))));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
    }
    @Test
    public void getAddresses_AddressIsNotFound_Expect200WithEmptyList() throws Exception {
        Long customerId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/address", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BASE))));
    }

    @Test
    public void updateAddress_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        Map<String, Object> addressResponse = new ChubFactoryUtil().buildAsAddress();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(addressResponse))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void updateAddress_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void updateAddress_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();
        AllErrorsDTO errors = new AllErrorsDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse()
                        .withStatus(406)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(errors))));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].reasonCode", is(errors.getErrors().get(0).getReasonCode())));

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
    @Test
    public void updateAddress_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        Long addressId = random.nextLong();
        AddressDTO dto = new AddressDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/address/%d", customerId, addressId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(putRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.CUSTOMER_ADDRESS_BY_ID))));
    }
}
