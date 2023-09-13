package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.CustomerSubscriptionsDTO;
import com.panera.cmt.dto.proxy.chub.CustomerSubscriptions;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.service.chub.ICustomerSubscriptionService;
import com.panera.cmt.test_builders.CustomerSubscriptionsBuilder;
import com.panera.cmt.test_builders.CustomerSubscriptionsDTOBuilder;
import com.panera.cmt.test_builders.ResponseHolderBuilder;
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

import static com.panera.cmt.test_util.SharedTestUtil.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerSubscriptionsController.class)
public class CustomerSubscriptionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerSubscriptionService customerSubscriptionService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getSubscriptions_SubscriptionsAreFound_Expect200() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptions entity = new CustomerSubscriptionsBuilder().build();

        when(customerSubscriptionService.getSubscriptions(eq(customerId))).thenReturn(Optional.of(entity));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/subscriptions", customerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.subscriptions", hasSize(1)))
                .andExpect(jsonPath("$.subscriptions[0].subscriptionCode", is(entity.getSubscriptions().get(0).getSubscriptionCode())))
                .andExpect(jsonPath("$.subscriptions[0].displayName", is(entity.getSubscriptions().get(0).getDisplayName())))
                .andExpect(jsonPath("$.subscriptions[0].isSubscribed", is(entity.getSubscriptions().get(0).isSubscribed())))
                .andExpect(jsonPath("$.suppressors", hasSize(1)))
                .andExpect(jsonPath("$.suppressors[0].suppressionCode", is(entity.getSuppressors().get(0).getSuppressionCode())))
                .andExpect(jsonPath("$.suppressors[0].displayName", is(entity.getSuppressors().get(0).getDisplayName())))
                .andExpect(jsonPath("$.suppressors[0].isSuppressed", is(entity.getSuppressors().get(0).isSuppressed())));


        verify(customerSubscriptionService, times(1)).getSubscriptions(anyLong());
    }
    @Test
    public void getSubscriptions_SubscriptionsAreNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();

        when(customerSubscriptionService.getSubscriptions(eq(customerId))).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/subscriptions", customerId)))
                .andExpect(status().isNotFound());


        verify(customerSubscriptionService, times(1)).getSubscriptions(anyLong());
    }

    @Test
    public void updateSubscriptions_SuccessfullyUpdated_Expect204() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();
        ResponseHolder<CustomerSubscriptions> responseHolder = new ResponseHolderBuilder<CustomerSubscriptions>().withStatus(HttpStatus.NO_CONTENT).build();

        when(customerSubscriptionService.updateSubscriptions(eq(customerId), any(CustomerSubscriptions.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerSubscriptionService, times(1)).updateSubscriptions(anyLong(), any(CustomerSubscriptions.class));
    }
    @Test
    public void updateSubscriptions_CustomerIsNotFound_Expect404() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();
        ResponseHolder<CustomerSubscriptions> responseHolder = new ResponseHolderBuilder<CustomerSubscriptions>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerSubscriptionService.updateSubscriptions(eq(customerId), any(CustomerSubscriptions.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerSubscriptionService, times(1)).updateSubscriptions(anyLong(), any(CustomerSubscriptions.class));
    }
    @Test
    public void updateSubscriptions_ValidationError_Expect406() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();
        ResponseHolder<CustomerSubscriptions> responseHolder = new ResponseHolderBuilder<CustomerSubscriptions>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerSubscriptionService.updateSubscriptions(eq(customerId), any(CustomerSubscriptions.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerSubscriptionService, times(1)).updateSubscriptions(anyLong(), any(CustomerSubscriptions.class));
    }
    @Test
    public void updateSubscriptions_ResponseIsEmpty_Expect500() throws Exception {
        Long customerId = random.nextLong();
        CustomerSubscriptionsDTO dto = new CustomerSubscriptionsDTOBuilder().build();

        when(customerSubscriptionService.updateSubscriptions(eq(customerId), any(CustomerSubscriptions.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d/subscriptions", customerId))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerSubscriptionService, times(1)).updateSubscriptions(anyLong(), any(CustomerSubscriptions.class));
    }
}