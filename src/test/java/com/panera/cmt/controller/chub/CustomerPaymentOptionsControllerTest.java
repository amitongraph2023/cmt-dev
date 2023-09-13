package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.CreditCardDTO;
import com.panera.cmt.dto.chub.GiftCardDTO;
import com.panera.cmt.dto.chub.PayPalDTO;
import com.panera.cmt.dto.proxy.chub.*;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.PaymentOptionType;
import com.panera.cmt.service.chub.ICustomerPaymentService;
import com.panera.cmt.test_builders.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerPaymentOptionsController.class)
public class CustomerPaymentOptionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerPaymentService customerPaymentService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addGiftCard_SuccessfullyCreated_Expect201() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();
        GiftCard giftCard = new GiftCardBuilder().build(true);
        ResponseHolder<GiftCard> responseHolder = new ResponseHolderBuilder<GiftCard>().withEntity(giftCard).build();

        when(customerPaymentService.addPaymentOption(eq(customerId), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated());

        verify(customerPaymentService, times(1)).addPaymentOption(anyLong(), any(PaymentOptionType.class), any(GiftCard.class));
    }
    @Test
    public void addGiftCard_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();
        ResponseHolder<GiftCard> responseHolder = new ResponseHolderBuilder<GiftCard>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPaymentService.addPaymentOption(eq(customerId), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).addPaymentOption(anyLong(), any(PaymentOptionType.class), any(GiftCard.class));
    }
    @Test
    public void addGiftCard_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();
        ResponseHolder<GiftCard> responseHolder = new ResponseHolderBuilder<GiftCard>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPaymentService.addPaymentOption(eq(customerId), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerPaymentService, times(1)).addPaymentOption(anyLong(), any(PaymentOptionType.class), any(GiftCard.class));
    }
    @Test
    public void addGiftCard_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        when(customerPaymentService.addPaymentOption(eq(customerId), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.empty());

        mockMvc.perform(post(String.format("/api/v1/customer/%d/paymentoptions/giftcard", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPaymentService, times(1)).addPaymentOption(anyLong(), any(PaymentOptionType.class), any(GiftCard.class));
    }

    @Test
    public void deletePaymentOption_SuccessfullyDeleted_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        ResponseHolder<Object> responseHolder = new ResponseHolderBuilder<>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPaymentService.deletePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isNoContent());

        verify(customerPaymentService, times(1)).deletePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void deletePaymentOption_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        ResponseHolder<Object> responseHolder = new ResponseHolderBuilder<>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPaymentService.deletePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).deletePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void deletePaymentOption_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;
        ResponseHolder<Object> responseHolder = new ResponseHolderBuilder<>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPaymentService.deletePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isNotAcceptable());

        verify(customerPaymentService, times(1)).deletePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void deletePaymentOption_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PaymentOptionType type = PaymentOptionType.creditcard;

        when(customerPaymentService.deletePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard))).thenReturn(Optional.empty());

        mockMvc.perform(delete(String.format("/api/v1/customer/%d/paymentoptions/%s/%s", customerId, type, id)))
                .andExpect(status().isInternalServerError());

        verify(customerPaymentService, times(1)).deletePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }

    @Test
    public void getApplePay_ApplePayIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        ApplePay entity = new ApplePayBuilder().build();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.applepay))).thenReturn(Optional.of(entity));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/applepay/%s", customerId, id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.accountNumber", is(entity.getAccountNumber())));

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void getApplePay_ApplePayIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.applepay))).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/applepay/%s", customerId, id)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void getCreditCard_CreditCardIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        CreditCard entity = new CreditCardBuilder().build();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard))).thenReturn(Optional.of(entity));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/creditcard/%s", customerId, id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.token", is(entity.getToken())))
                .andExpect(jsonPath("$.expirationDate", is(entity.getExpirationDate())))
                .andExpect(jsonPath("$.cardholderName", is(entity.getCardholderName())))
                .andExpect(jsonPath("$.lastFour", is(entity.getLastFour())))
                .andExpect(jsonPath("$.creditCardType", is(entity.getCreditCardType())))
                .andExpect(jsonPath("$.paymentProcessor", is(entity.getPaymentProcessor())))
                .andExpect(jsonPath("$.paymentLabel", is(entity.getPaymentLabel())))
                .andExpect(jsonPath("$.isDefault", is(entity.isDefault())));

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void getCreditCard_CreditCardIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard))).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/creditcard/%s", customerId, id)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }

    @Test
    public void getGiftCard_GiftCardIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        GiftCard entity = new GiftCardBuilder().build();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.giftcard))).thenReturn(Optional.of(entity));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/giftcard/%s", customerId, id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.cardNumber", is(entity.getCardNumber())))
                .andExpect(jsonPath("$.cardNickname", is(entity.getCardNickname())));

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void getGiftCard_GiftCardIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.giftcard))).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/giftcard/%s", customerId, id)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }

    @Test
    public void getPayPal_PayPalIsFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PayPal entity = new PayPalBuilder().build();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.paypal))).thenReturn(Optional.of(entity));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/paypal/%s", customerId, id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.accountNumber", is(entity.getAccountNumber())))
                .andExpect(jsonPath("$.username", is(entity.getUsername())));

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }
    @Test
    public void getPayPal_PayPalIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();

        when(customerPaymentService.getPaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.paypal))).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions/paypal/%s", customerId, id)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).getPaymentOption(anyLong(), anyString(), any(PaymentOptionType.class));
    }

    @Test
    public void getPaymentOptions_PaymentOptionsAreFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        PaymentOptions entity = new PaymentOptionsBuilder().build();

        when(customerPaymentService.getPaymentOptions(eq(customerId))).thenReturn(Optional.of(entity));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.corporateCateringAccounts", hasSize(1)))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].orgNumber", is(entity.getCorporateCateringAccounts().get(0).getOrgNumber())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].ccaNumber", is(entity.getCorporateCateringAccounts().get(0).getCcaNumber())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].ccaBillingName", is(entity.getCorporateCateringAccounts().get(0).getCcaBillingName())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].addressLine1", is(entity.getCorporateCateringAccounts().get(0).getAddressLine1())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].addressLine2", is(entity.getCorporateCateringAccounts().get(0).getAddressLine2())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].city", is(entity.getCorporateCateringAccounts().get(0).getCity())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].state", is(entity.getCorporateCateringAccounts().get(0).getState())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].zipCode", is(entity.getCorporateCateringAccounts().get(0).getZipCode())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].country", is(entity.getCorporateCateringAccounts().get(0).getCountry())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].poRequired", is(entity.getCorporateCateringAccounts().get(0).getPoRequired())))
                .andExpect(jsonPath("$.corporateCateringAccounts[0].onlineEnabled", is(entity.getCorporateCateringAccounts().get(0).getOnlineEnabled())))
                .andExpect(jsonPath("$.creditCards", hasSize(1)))
                .andExpect(jsonPath("$.creditCards[0].token", is(entity.getCreditCards().get(0).getToken())))
                .andExpect(jsonPath("$.creditCards[0].expirationDate", is(entity.getCreditCards().get(0).getExpirationDate())))
                .andExpect(jsonPath("$.creditCards[0].cardholderName", is(entity.getCreditCards().get(0).getCardholderName())))
                .andExpect(jsonPath("$.creditCards[0].lastFour", is(entity.getCreditCards().get(0).getLastFour())))
                .andExpect(jsonPath("$.creditCards[0].creditCardType", is(entity.getCreditCards().get(0).getCreditCardType())))
                .andExpect(jsonPath("$.creditCards[0].paymentProcessor", is(entity.getCreditCards().get(0).getPaymentProcessor())))
                .andExpect(jsonPath("$.creditCards[0].paymentLabel", is(entity.getCreditCards().get(0).getPaymentLabel())))
                .andExpect(jsonPath("$.creditCards[0].isDefault", is(entity.getCreditCards().get(0).isDefault())))
                .andExpect(jsonPath("$.giftCards", hasSize(1)))
                .andExpect(jsonPath("$.giftCards[0].cardNumber", is(entity.getGiftCards().get(0).getCardNumber())))
                .andExpect(jsonPath("$.giftCards[0].cardNickname", is(entity.getGiftCards().get(0).getCardNickname())))
                .andExpect(jsonPath("$.payPals", hasSize(1)))
                .andExpect(jsonPath("$.payPals[0].accountNumber", is(entity.getPayPals().get(0).getAccountNumber())))
                .andExpect(jsonPath("$.payPals[0].username", is(entity.getPayPals().get(0).getUsername())));

        verify(customerPaymentService, times(1)).getPaymentOptions(anyLong());
    }
    @Test
    public void getPaymentOptions_PaymentOptionsAreNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        when(customerPaymentService.getPaymentOptions(eq(customerId))).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/paymentoptions", customerId)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).getPaymentOptions(anyLong());
    }

    @Test
    public void updateCreditCard_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        CreditCardDTO dto = new CreditCardDTOBuilder().build();
        ResponseHolder<CreditCard> responseHolder = new ResponseHolderBuilder<CreditCard>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard), any(CreditCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/creditcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(CreditCard.class));
    }
    @Test
    public void updateCreditCard_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        CreditCardDTO dto = new CreditCardDTOBuilder().build();
        ResponseHolder<CreditCard> responseHolder = new ResponseHolderBuilder<CreditCard>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard), any(CreditCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/creditcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(CreditCard.class));
    }
    @Test
    public void updateCreditCard_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        CreditCardDTO dto = new CreditCardDTOBuilder().build();
        ResponseHolder<CreditCard> responseHolder = new ResponseHolderBuilder<CreditCard>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard), any(CreditCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/creditcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(CreditCard.class));
    }
    @Test
    public void updateCreditCard_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        CreditCardDTO dto = new CreditCardDTOBuilder().build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.creditcard), any(CreditCard.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/creditcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(CreditCard.class));
    }

    @Test
    public void updateGiftCard_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();
        ResponseHolder<GiftCard> responseHolder = new ResponseHolderBuilder<GiftCard>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/giftcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(GiftCard.class));
    }
    @Test
    public void updateGiftCard_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();
        ResponseHolder<GiftCard> responseHolder = new ResponseHolderBuilder<GiftCard>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/giftcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(GiftCard.class));
    }
    @Test
    public void updateGiftCard_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();
        ResponseHolder<GiftCard> responseHolder = new ResponseHolderBuilder<GiftCard>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/giftcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(GiftCard.class));
    }
    @Test
    public void updateGiftCard_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        GiftCardDTO dto = new GiftCardDTOBuilder().build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.giftcard), any(GiftCard.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/giftcard/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(GiftCard.class));
    }

    @Test
    public void updatePayPal_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PayPalDTO dto = new PayPalDTOBuilder().build();
        ResponseHolder<PayPal> responseHolder = new ResponseHolderBuilder<PayPal>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.paypal), any(PayPal.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/paypal/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(PayPal.class));
    }
    @Test
    public void updatePayPal_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PayPalDTO dto = new PayPalDTOBuilder().build();
        ResponseHolder<PayPal> responseHolder = new ResponseHolderBuilder<PayPal>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.paypal), any(PayPal.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/paypal/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(PayPal.class));
    }
    @Test
    public void updatePayPal_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PayPalDTO dto = new PayPalDTOBuilder().build();
        ResponseHolder<PayPal> responseHolder = new ResponseHolderBuilder<PayPal>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.paypal), any(PayPal.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/paypal/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(PayPal.class));
    }
    @Test
    public void updatePayPal_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        String id = UUID.randomUUID().toString();
        PayPalDTO dto = new PayPalDTOBuilder().build();

        when(customerPaymentService.updatePaymentOption(eq(customerId), eq(id), eq(PaymentOptionType.paypal), any(PayPal.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/paymentoptions/paypal/%s", customerId, id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerPaymentService, times(1)).updatePaymentOption(anyLong(), anyString(), any(PaymentOptionType.class), any(PayPal.class));
    }
}