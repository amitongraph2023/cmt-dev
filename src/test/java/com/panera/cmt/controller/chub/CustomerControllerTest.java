package com.panera.cmt.controller.chub;

import com.panera.cmt.dto.chub.CustomerDTO;
import com.panera.cmt.dto.proxy.chub.Customer;
import com.panera.cmt.dto.proxy.chub.CustomerDetails;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.Role;
import com.panera.cmt.enums.UpdateAccountStatusAction;
import com.panera.cmt.filter.AuthenticatedUserManager;
import com.panera.cmt.mongo.entity.AuthenticatedUser;
import com.panera.cmt.service.chub.ICustomerService;
import com.panera.cmt.test_builders.*;
import org.junit.Before;
import org.junit.Ignore;
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
import static com.panera.cmt.test_util.SharedTestUtil.isIntLong;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ICustomerService customerService;

    private Random random = new Random();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        AuthenticatedUser authenticatedUser = new AuthenticatedUserBuilder().withRole(Role.ADMIN).build();
        AuthenticatedUserManager.setAuthenticatedUser(authenticatedUser);
    }

    @Test
    public void getCustomer_CustomerIsFound_Expect200() throws Exception {
        Long id = random.nextLong();
        Customer customer = new CustomerBuilder().build(true);

        when(customerService.getCustomer(id)).thenReturn(Optional.of(customer));

        mockMvc.perform(get(String.format("/api/v1/customer/%d", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.customerId", isIntLong(customer.getCustomerId())))
                .andExpect(jsonPath("$.username", is(customer.getUsername())))
                .andExpect(jsonPath("$.firstName", is(customer.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(customer.getLastName())))
                .andExpect(jsonPath("$.isSmsGlobalOpt", is(customer.isSmsGlobalOpt())))
                .andExpect(jsonPath("$.isEmailGlobalOpt", is(customer.isEmailGlobalOpt())))
                .andExpect(jsonPath("$.isMobilePushOpt", is(customer.isMobilePushOpt())));

        verify(customerService, times(1)).getCustomer(anyLong());
    }
    @Test
    public void getCustomer_CustomerIsFound_Expect404() throws Exception {
        Long id = random.nextLong();

        when(customerService.getCustomer(id)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d", id)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomer(anyLong());
    }

    @Test
    public void getCustomerDetails_CustomerIsFound_Expect200() throws Exception {
        Long id = random.nextLong();
        CustomerDetails customerDetails = new CustomerDetailsBuilder().build(true);

        when(customerService.getCustomerDetails(id)).thenReturn(Optional.of(customerDetails));

        mockMvc.perform(get(String.format("/api/v1/customer/%d/details", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.customerId", isIntLong(customerDetails.getCustomerId())))
                .andExpect(jsonPath("$.username", is(customerDetails.getUsername())))
                .andExpect(jsonPath("$.firstName", is(customerDetails.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(customerDetails.getLastName())))
                .andExpect(jsonPath("$.isSmsGlobalOpt", is(customerDetails.isSmsGlobalOpt())))
                .andExpect(jsonPath("$.isEmailGlobalOpt", is(customerDetails.isEmailGlobalOpt())))
                .andExpect(jsonPath("$.isMobilePushOpt", is(customerDetails.isMobilePushOpt())))
                .andExpect(jsonPath("$.loyaltyCardNumber", is(customerDetails.getLoyaltyCardNumber())))
                .andExpect(jsonPath("$.dob", is(customerDetails.getDob())))
                .andExpect(jsonPath("$.socialIntegration.facebookIntegration.facebookId", is(customerDetails.getSocialIntegration().getFacebookIntegration().getFacebookId())))
                .andExpect(jsonPath("$.socialIntegration.googleIntegration.googleId", is(customerDetails.getSocialIntegration().getGoogleIntegration().getGoogleId())))
                .andExpect(jsonPath("$.taxExemptions", hasSize(1)))
                .andExpect(jsonPath("$.taxExemptions[0].company", is(customerDetails.getTaxExemptions().get(0).getCompany())))
                .andExpect(jsonPath("$.taxExemptions[0].state", is(customerDetails.getTaxExemptions().get(0).getState())))
                .andExpect(jsonPath("$.taxExemptions[0].country", is(customerDetails.getTaxExemptions().get(0).getCountry())));

        verify(customerService, times(1)).getCustomerDetails(anyLong());
    }
    @Test
    public void getCustomerDetails_CustomerIsFound_Expect404() throws Exception {
        Long id = random.nextLong();

        when(customerService.getCustomerDetails(id)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/customer/%d/details", id)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerDetails(anyLong());
    }

    @Test
    public void updateAccountStatus_ReinstateAccount_Expect200() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.REINSTATE;

        when(customerService.updateAccountStatus(id, action)).thenReturn(true);

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.name())))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateAccountStatus(anyLong(), any(UpdateAccountStatusAction.class));
    }
    @Test
    public void updateAccountStatus_SuspendAccount_Expect200() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.SUSPEND;

        when(customerService.updateAccountStatus(id, action)).thenReturn(true);

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.name())))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateAccountStatus(anyLong(), any(UpdateAccountStatusAction.class));
    }
    @Test
    public void updateAccountStatus_TerminateAccount_Expect200() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.TERMINATE;

        when(customerService.updateAccountStatus(id, action)).thenReturn(true);

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.name())))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateAccountStatus(anyLong(), any(UpdateAccountStatusAction.class));
    }
    @Test
    public void updateAccountStatus_NoAction_Expect400() throws Exception {
        Long id = random.nextLong();

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status", id)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(0)).updateAccountStatus(anyLong(), any(UpdateAccountStatusAction.class));
    }
    @Test
    public void updateAccountStatus_InvalidAccount_Expect406() throws Exception {
        Long id = random.nextLong();
        UpdateAccountStatusAction action = UpdateAccountStatusAction.REINSTATE;

        mockMvc.perform(post(String.format("/api/v1/customer/%d/status?action=%s", id, action.getRouteParamName())))
                .andExpect(status().isNotAcceptable());

        verify(customerService, times(0)).updateAccountStatus(anyLong(), any(UpdateAccountStatusAction.class));
    }

    @Test
    public void updateCustomer_SuccessfullyUpdated_Expect204() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();
        ResponseHolder<Customer> responseHolder = new ResponseHolderBuilder<Customer>().build();

        when(customerService.updateCustomer(eq(id), any(Customer.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(Customer.class));
    }
    @Test
    public void updateCustomer_CustomerIsNotFound_Expect404() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();
        ResponseHolder<Customer> responseHolder = new ResponseHolderBuilder<Customer>().withStatus(HttpStatus.NOT_FOUND).build();

        when(customerService.updateCustomer(eq(id), any(Customer.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(Customer.class));
    }
    @Test
    public void updateCustomer_ValidationError_Expect406() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();
        ResponseHolder<Customer> responseHolder = new ResponseHolderBuilder<Customer>().withStatus(HttpStatus.NOT_ACCEPTABLE).build();

        when(customerService.updateCustomer(eq(id), any(Customer.class))).thenReturn(Optional.of(responseHolder));

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isNotAcceptable());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(Customer.class));
    }
    @Test
    public void updateCustomer_ResponseIsEmpty_Expect500() throws Exception {
        Long id = random.nextLong();
        CustomerDTO dto = new CustomerDTOBuilder().build();

        when(customerService.updateCustomer(eq(id), any(Customer.class))).thenReturn(Optional.empty());

        mockMvc.perform(put(String.format("/api/v1/customer/%d", id))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(Customer.class));
    }
}