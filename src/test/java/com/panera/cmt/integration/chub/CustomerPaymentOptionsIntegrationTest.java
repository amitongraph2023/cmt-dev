package com.panera.cmt.integration.chub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.panera.cmt.Application;
import com.panera.cmt.controller.sso.SSOController;
import com.panera.cmt.dto.chub.CreditCardDTO;
import com.panera.cmt.dto.chub.GiftCardDTO;
import com.panera.cmt.dto.chub.PayPalDTO;
import com.panera.cmt.enums.ChubEndpoints;
import com.panera.cmt.enums.PaymentOptionType;
import com.panera.cmt.service.chub.ICustomerPaymentService;
import com.panera.cmt.test_builders.CreditCardDTOBuilder;
import com.panera.cmt.test_builders.GiftCardDTOBuilder;
import com.panera.cmt.test_builders.PayPalDTOBuilder;
import com.panera.cmt.test_util.ChubFactoryUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static com.panera.cmt.util.SharedUtils.transformEndpoint;
import static com.panera.cmt.util.SharedUtils.transformPaymentOptionsEndpoint;
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
@SuppressWarnings("unchecked")
@Transactional
public class CustomerPaymentOptionsIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Autowired
    private ICustomerPaymentService customerPaymentService;

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
        ReflectionTestUtils.setField(customerPaymentService, "baseUrl", "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void addGiftCard_SuccessfullyCreated_Expect201() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard)))
                .willReturn(aResponse().withStatus(201)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        WireMock.verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard))));
    }
    @Test
    public void addGiftCard_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard))));
    }
    @Test
    public void addGiftCard_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard)))
                .willReturn(aResponse().withStatus(406)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        WireMock.verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard))));
    }
    @Test
    public void addGiftCard_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.post(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(postRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE, PaymentOptionType.giftcard))));
    }

    @Test
    public void deletePaymentOption_SuccessfullyDeleted_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(WireMock.delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isNoContent());

        WireMock.verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void deletePaymentOption_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(WireMock.delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isNotFound());

        WireMock.verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void deletePaymentOption_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(WireMock.delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(406)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isNotAcceptable());

        WireMock.verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void deletePaymentOption_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(WireMock.delete(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(deleteRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }

    @Test
    public void getCreditCard_CreditCardIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsCreditCard();

        stubFor(WireMock.get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.token", is(response.get("token"))))
                .andExpect(jsonPath("$.expirationDate", is(response.get("expirationDate"))))
                .andExpect(jsonPath("$.cardholderName", is(response.get("cardholderName"))))
                .andExpect(jsonPath("$.lastFour", is(response.get("lastFour"))))
                .andExpect(jsonPath("$.creditCardType", is(response.get("creditCardType"))))
                .andExpect(jsonPath("$.paymentProcessor", is(response.get("paymentProcessor"))))
                .andExpect(jsonPath("$.paymentLabel", is(response.get("paymentLabel"))))
                .andExpect(jsonPath("$.isDefault", is(response.get("isDefault"))));

        WireMock.verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void getCreditCard_CreditCardIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        stubFor(WireMock.get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id)))
                .andExpect(status().isNotFound());

        WireMock.verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }

    @Test
    public void getGiftCard_GiftCardIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;
        Map<String, Object> response = new ChubFactoryUtil().buildAsGiftCard();

        stubFor(WireMock.get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.cardNumber", is(response.get("cardNumber"))))
                .andExpect(jsonPath("$.cardNickname", is(response.get("cardNickname"))));

        WireMock.verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void getGiftCard_GiftCardIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.giftcard;

        stubFor(WireMock.get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id)))
                .andExpect(status().isNotFound());

        WireMock.verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }

    @Test
    public void getPayPal_PayPalIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.paypal;
        Map<String, Object> response = new ChubFactoryUtil().buildAsPayPal();

        stubFor(WireMock.get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.accountNumber", is(response.get("accountNumber"))))
                .andExpect(jsonPath("$.username", is(response.get("username"))));

        WireMock.verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void getPayPal_PayPalIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.paypal;

        stubFor(WireMock.get(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id)))
                .andExpect(status().isNotFound());

        WireMock.verify(getRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }

    @Test
    public void getPaymentOptions_PaymentOptionsAreFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        Map<String, Object> response = new ChubFactoryUtil().buildAsPaymentOptions();
        Map<String, Object> cca = (Map) ((List)response.get("corporateCateringAccounts")).get(0);
        Map<String, Object> creditCard = (Map) ((List)response.get("creditCards")).get(0);
        Map<String, Object> giftCard = (Map) ((List)response.get("giftCards")).get(0);
        Map<String, Object> payPal = (Map) ((List)response.get("payPals")).get(0);

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(asJsonString(response))));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.corporateCateringAccounts", hasSize(1)))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].orgNumber", is(cca.get("orgNumber"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].ccaNumber", is(cca.get("ccaNumber"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].ccaBillingName", is(cca.get("ccaBillingName"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].addressLine1", is(cca.get("addressLine1"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].addressLine2", is(cca.get("addressLine2"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].city", is(cca.get("city"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].state", is(cca.get("state"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].zipCode", is(cca.get("zipCode"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].country", is(cca.get("country"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].poRequired", is(cca.get("poRequired"))))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].onlineEnabled", is(cca.get("onlineEnabled"))))
                .andExpect(jsonPath("$.creditCards", hasSize(1)))
                .andExpect(jsonPath("$.creditCards[0].token", is(creditCard.get("token"))))
                .andExpect(jsonPath("$.creditCards[0].expirationDate", is(creditCard.get("expirationDate"))))
                .andExpect(jsonPath("$.creditCards[0].cardholderName", is(creditCard.get("cardholderName"))))
                .andExpect(jsonPath("$.creditCards[0].lastFour", is(creditCard.get("lastFour"))))
                .andExpect(jsonPath("$.creditCards[0].creditCardType", is(creditCard.get("creditCardType"))))
                .andExpect(jsonPath("$.creditCards[0].paymentProcessor", is(creditCard.get("paymentProcessor"))))
                .andExpect(jsonPath("$.creditCards[0].paymentLabel", is(creditCard.get("paymentLabel"))))
                .andExpect(jsonPath("$.creditCards[0].isDefault", is(creditCard.get("isDefault"))))
                .andExpect(jsonPath("$.giftCards", hasSize(1)))
                .andExpect(jsonPath("$.giftCards[0].cardNumber", is(giftCard.get("cardNumber"))))
                .andExpect(jsonPath("$.giftCards[0].cardNickname", is(giftCard.get("cardNickname"))))
                .andExpect(jsonPath("$.payPals", hasSize(1)))
                .andExpect(jsonPath("$.payPals[0].accountNumber", is(payPal.get("accountNumber"))))
                .andExpect(jsonPath("$.payPals[0].username", is(payPal.get("username"))));

        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE))));
    }
    @Test
    public void getPaymentOptions_PaymentOptionsAreNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        stubFor(WireMock.get(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions", customerId)))
                .andExpect(status().isNotFound());

        WireMock.verify(getRequestedFor(urlPathMatching(transformEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BASE))));
    }

    @Test
    public void updateCreditCard_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        CreditCardDTO dto = new CreditCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updateCreditCard_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        CreditCardDTO dto = new CreditCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updateCreditCard_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        CreditCardDTO dto = new CreditCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(406)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updateCreditCard_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        CreditCardDTO dto = new CreditCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }

    @Test
    public void updateGiftCard_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updateGiftCard_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updateGiftCard_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(406)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updateGiftCard_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }

    @Test
    public void updatePayPal_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        PayPalDTO dto = new PayPalDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(204)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updatePayPal_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        PayPalDTO dto = new PayPalDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updatePayPal_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        PayPalDTO dto = new PayPalDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(406)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
    @Test
    public void updatePayPal_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        PayPalDTO dto = new PayPalDTOBuilder().build();

        stubFor(WireMock.put(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type.name(), id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        WireMock.verify(putRequestedFor(urlPathMatching(transformPaymentOptionsEndpoint(ChubEndpoints.PAYMENT_OPTIONS_BY_TYPE_AND_VALUE, type))));
    }
}
